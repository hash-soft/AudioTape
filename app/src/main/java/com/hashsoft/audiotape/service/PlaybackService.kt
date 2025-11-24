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
import com.hashsoft.audiotape.core.extensions.playingContentPositionFlow
import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.AudioTapeStagingRepository
import com.hashsoft.audiotape.data.ContentPositionRepository
import com.hashsoft.audiotape.data.ControllerStateRepository
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
    lateinit var _contentPositionRepository: ContentPositionRepository

    @Inject
    lateinit var _audioTapeRepository: AudioTapeRepository

    @Inject
    lateinit var _audioStoreRepository: AudioStoreRepository

    @Inject
    lateinit var _playingStateRepository: PlayingStateRepository

    @Inject
    lateinit var _controllerStateRepository: ControllerStateRepository

    @Inject
    lateinit var _audioTapeStagingRepository: AudioTapeStagingRepository


    // Create your player and media session in the onCreate lifecycle event
    @androidx.annotation.OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        Timber.d("onCreate")
        val player = ExoPlayer.Builder(this)
            .setHandleAudioBecomingNoisy(true).build()
        setPlayerListener(player)
        mediaSession =
            MediaSession.Builder(this, player)
                .setCallback(
                    MediaSessionCallback(
                        _audioStoreRepository,
                        _playingStateRepository,
                        _audioTapeRepository
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
        observeState(player)
    }


    private fun observeState(player: ExoPlayer) {
        serviceScope.launch {
            // UIとサービスからの変更をここで監視してaudioTapeを更新する
            _audioTapeStagingRepository.data.collect { staging ->
                // 何らかの異常がなければキーは存在しているはず
                _audioTapeRepository.updatePlayingPosition(
                    staging.folderPath,
                    staging.currentName,
                    staging.position
                )
            }
        }
        serviceScope.launch {
            // Todo バックグラウンドでも動いてしまうのでModelViewに移動したい
            player.playingContentPositionFlow(_controllerStateRepository.data).collect { position ->
                Timber.d("position1 = $position")
                _contentPositionRepository.update(position)
            }
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
                val audioTape = playerToAudioTape(player)
                runBlocking {
                    _audioTapeRepository.updatePlayingPosition(
                        audioTape.folderPath,
                        audioTape.currentName,
                        audioTape.position
                    )
                }
            }
            release()
        }
        mediaSession?.player?.release()
        super.onDestroy()
    }


    private fun playerToAudioTape(player: Player): AudioTapeDto {
        val position = player.contentPosition
        val uri = player.currentMediaItem?.localConfiguration?.uri
        return if (uri != null) {
            val file = File(_audioStoreRepository.uriToPath(uri))
            AudioTapeDto(file.parent ?: "", file.name, position)
        } else {
            AudioTapeDto("", "", 0)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun setPlayerListener(player: ExoPlayer) {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                _controllerStateRepository.updateIsPlaying(isPlaying)
                _audioTapeStagingRepository.updatePosition(player.contentPosition)

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
                        // prepare前
                        // 1度prepareしないとこのコールバック来ない
                        // stopやエラーで停止後に来る
                        // ここに来たらprepareが必要
                        // Todo stopはしないがエラーは発生するのでどこでprepareをすべきか
                        // MediaItemがあったらprepareを試してみる
                        Timber.d("#2 state idle")
                        _controllerStateRepository.updateIsReadyOk(false)
                    }

                    Player.STATE_BUFFERING -> {
                        Timber.d("#2 state buffering position = ${player.contentPosition}")
                        // 再生可能にする準備中
                        _controllerStateRepository.updateIsReadyOk(false)
                    }

                    Player.STATE_READY -> {
                        // 再生可能
                        // controllerを操作できるようになる
                        // playWhenReady:trueなら再生中 falseなら停止中
                        // seek操作を反映するために必要
                        Timber.d("#2 state ready position = ${player.contentPosition}")
                        _controllerStateRepository.updateIsReadyOk(true)
                        _audioTapeStagingRepository.updatePosition(player.contentPosition)
                    }


                    Player.STATE_ENDED -> {
                        Timber.d("#2 state ended")
                        // 終了だが条件がわかっていない
                        // stopしたとき？
                        // 要素が0のときもくる
                        // 要素が0のときにフォアグラウンドサービスを止めるのがいいか
                        _controllerStateRepository.updateIsReadyOk(false)
                    }
                }
            }

            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                super.onTimelineChanged(timeline, reason)
                Timber.d("#2 listener timelineChanged reason = $reason")
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Timber.d("#2 listener error = $error")
                // Todo エラー発生したメディアを無視しt次にすすめるか
                // この後idleになる
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                mediaMetadata.let {
                    Timber.d("#2 MediaMetadataChanged title: ${it.title}, artist: ${it.artist}, album: ${it.albumTitle}")
                }
                super.onMediaMetadataChanged(mediaMetadata)

            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                Timber.d("#2 MediaItemTransition = ${mediaItem?.mediaId} reason = $reason position = ${player.contentPosition}")
                super.onMediaItemTransition(mediaItem, reason)

                when (reason) {
                    Player.MEDIA_ITEM_TRANSITION_REASON_AUTO,// 曲が自動で変わったとき
                    Player.MEDIA_ITEM_TRANSITION_REASON_SEEK,// Seekで曲が変わったとき
                    Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED // プレイリストが変わったとき
                        -> {
                        changeMediaItem()
                    }

                    else -> {}

                }
            }

            private fun changeMediaItem() {
                val uri = player.currentMediaItem?.localConfiguration?.uri
                if (uri != null) {
                    val file = File(_audioStoreRepository.uriToPath(uri))
                    _audioTapeStagingRepository.updateAll(
                        folderPath = file.parent ?: "",
                        currentName = file.name,
                        player.contentPosition
                    )
                }
            }

        })
    }
}
