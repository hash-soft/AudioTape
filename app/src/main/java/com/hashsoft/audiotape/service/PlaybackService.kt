package com.hashsoft.audiotape.service

import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getActivity
import android.content.Intent
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.hashsoft.audiotape.MainActivity
import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.ControllerRepository
import com.hashsoft.audiotape.data.PlaybackPositionSource
import com.hashsoft.audiotape.data.PlayingStateRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class PlaybackService : MediaSessionService() {
    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var mediaSession: MediaSession? = null

    private val serviceScope = CoroutineScope(Dispatchers.Unconfined)

    @Inject
    lateinit var audioTapeRepository: AudioTapeRepository

    @Inject
    lateinit var audioStoreRepository: AudioStoreRepository

    @Inject
    lateinit var playingStateRepository: PlayingStateRepository

    @Inject
    lateinit var controllerRepository: ControllerRepository

    // Create your player and media session in the onCreate lifecycle event
    @androidx.annotation.OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        Timber.d("onCreate")
        val player = ExoPlayer.Builder(this)
            .setHandleAudioBecomingNoisy(true).build()
        Timber.d("#2 onCreate: player = ${player.playbackState}")
        setPlayerListener(player)
        mediaSession =
            MediaSession.Builder(this, player)
                .setCallback(
                    MediaSessionCallback(
                        audioStoreRepository,
                        playingStateRepository,
                        audioTapeRepository
                    )
                ).build()
                .also { builder ->
                    val intent = getActivity(
                        this,
                        0,
                        Intent(this, MainActivity::class.java),
                        FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
                    )
                    builder.setSessionActivity(intent)
                }
    }


    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
    }

    // Remember to release the player and media session in onDestroy
    override fun onDestroy() {
        Timber.d("**onDestroy: mediaSession = $mediaSession")
        ioScope.cancel()
        mediaSession?.run {
            // 再生中の場合だけ再生情報の保存をする
            if (player.isPlaying) {
                runBlocking {
                    updateTapeCurrentPosition(player, false)
                }
            }
            release()
        }
        mediaSession?.player?.release()
        super.onDestroy()
    }

    private suspend fun updateTapeCurrentPosition(player: Player, isLastPlayedAt: Boolean) {
        player.currentMediaItem?.localConfiguration?.uri?.let { uri ->
            val file = File(audioStoreRepository.uriToPath(uri))
            audioTapeRepository.updatePlayingPosition(
                folderPath = file.parent ?: "",
                currentName = file.name,
                position = player.currentPosition,
                isLastPlayedAt = isLastPlayedAt
            )
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun setPlayerListener(player: ExoPlayer) {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                // Readyになったら再生される場合も再生扱いにする
                val uiPlaying = player.playWhenReady || isPlaying
                controllerRepository.updateIsPlaying(uiPlaying)
                updatePlaybackPositionSource(isPlaying, player.playWhenReady)
                updateTapePosition(isPlaying)

                // 停止中のseekは停止のままなのでここにはこない
                // 再生中のseekは一度停止して再生になる
                // false > true >
                // MEDIA_ITEM_TRANSITION_REASON_AUTOの順に発行され
                // trueの段階ではcurrentMediaItemは変わってない
                Timber.d("#2 listener isPlaying = $isPlaying, ${player.playWhenReady}, position = ${player.contentPosition}, duration = ${player.duration}")
            }

            // seek時にもくる
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when (playbackState) {
                    Player.STATE_IDLE -> {
                        // prepare前の状態
                        // 初期状態だがlistener設定時にコールバックは来ない
                        // ここに来たらprepareが必要
                        // Todo stopはしないがエラーは発生するのでどこでprepareをすべきか
                        // MediaItemがあったらprepareを試してみる
                        Timber.d("#2 state idle")
                    }

                    Player.STATE_BUFFERING -> {
                        Timber.d("#2 state buffering position = ${player.contentPosition}")
                        // 再生可能にする準備中
                    }

                    Player.STATE_READY -> {
                        // 再生可能
                        // controllerを操作できるようになる
                        // playWhenReady:trueなら再生中 falseなら停止中
                        // seek操作を反映するために必要
                        Timber.d("#2 state ready position = ${player.contentPosition}")
                    }


                    Player.STATE_ENDED -> {
                        Timber.d("#2 state ended")
                        // 終了だが条件がわかっていない
                        // stopしたときや要素0のとき
                    }
                }
            }

            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                super.onTimelineChanged(timeline, reason)
                // Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED = 0
                // Player.TIMELINE_CHANGE_REASON_SOURCE_UPDATE = 1
                Timber.d("#2 listener timelineChanged reason = $reason")
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Timber.d("#2 listener error = $error")
                // Todo エラー発生したメディアを無視しt次にすすめるか
                // この後idleになる
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                super.onMediaMetadataChanged(mediaMetadata)
                mediaMetadata.let {
                    Timber.d("#2 MediaMetadataChanged title: ${it.title}, artist: ${it.artist}, album: ${it.albumTitle}")
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                Timber.d("#2 MediaItemTransition = ${mediaItem?.mediaId} reason = $reason title=${mediaItem?.mediaMetadata?.title} position = ${player.contentPosition}, uri = ${player.currentMediaItem?.localConfiguration?.uri}")
                super.onMediaItemTransition(mediaItem, reason)

                when (reason) {
                    // 曲が自動で変わったとき
                    Player.MEDIA_ITEM_TRANSITION_REASON_AUTO -> {
                        updateTapePosition(false)
                    }

                    // Seekで曲が変わったとき
                    Player.MEDIA_ITEM_TRANSITION_REASON_SEEK -> {
                        updateTapePosition(false)
                        updatePlaybackPositionSource(player.isPlaying, player.playWhenReady)
                    }

                    // プレイリストが変わったとき
                    Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED -> {
                        updatePlaybackPositionSource(player.isPlaying, player.playWhenReady)
                    }

                    else -> {}

                }
            }

            private fun updatePlaybackPositionSource(isPlaying: Boolean, playWhenReady: Boolean) {
                if (player.availableCommands.contains(Player.COMMAND_GET_CURRENT_MEDIA_ITEM) && player.currentMediaItem != null) {
                    val uiPlaying = playWhenReady || isPlaying
                    controllerRepository.updatePlaybackPositionSource(if (uiPlaying) PlaybackPositionSource.Player else PlaybackPositionSource.PlayerOnce)
                } else {
                    controllerRepository.updatePlaybackPositionSource(PlaybackPositionSource.None)
                }
            }

            private fun updateTapePosition(isLastPlayedAt: Boolean) {
                serviceScope.launch {
                    updateTapeCurrentPosition(player, isLastPlayedAt)
                }
            }

        })
    }
}
