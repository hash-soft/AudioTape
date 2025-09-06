package com.hashsoft.audiotape.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getActivity
import android.content.Intent
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.hashsoft.audiotape.AudioTape
import com.hashsoft.audiotape.MainActivity
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.PlaybackRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.io.File



class PlaybackService : MediaSessionService() {
    private val metadataScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var mediaSession: MediaSession? = null

    private val serviceScope = CoroutineScope(Dispatchers.Unconfined)

    private lateinit var _playbackRepository: PlaybackRepository

    private lateinit var _audioTapeRepository: AudioTapeRepository

    // Create your player and media session in the onCreate lifecycle event
    @androidx.annotation.OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        Timber.d("**onCreate")
        _playbackRepository = (application as AudioTape).playbackRepository
        _audioTapeRepository = (application as AudioTape).databaseContainer.audioTapeRepository
        val player = ExoPlayer.Builder(this)
            .setHandleAudioBecomingNoisy(true).build()
        setPlayerListener(player)
        player.repeatMode = Player.REPEAT_MODE_ALL
        mediaSession =
            MediaSession.Builder(this, player).setCallback(MediaSessionCallback(metadataScope)).build()
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
        //pauseAllPlayersAndStopSelf()
        //isPlaybackOngoing()
        Timber.d("**onTaskRemoved mediaSession = $mediaSession")
        super.onTaskRemoved(rootIntent)
    }

    // Remember to release the player and media session in onDestroy
    override fun onDestroy() {
        Timber.d("**onDestroy: mediaSession = $mediaSession")
        metadataScope.cancel()
        mediaSession?.run {
            // 再生情報の保存をする UI状態はUIから行う
            //val audioTape = getCurrentAudioTape(player)
            val audioTape = playerToAudioTape(player)
            Timber.d("**destroy audiotape = $audioTape")
            //if (audioTape != null) {
            runBlocking {
                _audioTapeRepository.updateCurrentNamePosition(
                    audioTape.folderPath,
                    audioTape.currentName,
                    audioTape.position
                )
            }
            //}
            release()
        }
        mediaSession?.player?.release()
        super.onDestroy()
    }


    private fun playerToAudioTape(player: Player): AudioTapeDto {
        val position = player.contentPosition
        val mediaId = player.currentMediaItem?.mediaId ?: ""
        val file = File(mediaId)
        val parent = file.parent
        return AudioTapeDto(parent ?: "", file.name, position)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun setPlayerListener(player: ExoPlayer) {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                // 停止中のseekは停止のままなのでここにはこない
                // 再生中のseekは一度停止して再生になる
                // false > true >
                // MEDIA_ITEM_TRANSITION_REASON_AUTOの順に発行され
                // trueの段階ではcurrentMediaItemは変わってない
                //_playbackRepository.updatePlayingPosition(isPlaying, player.contentPosition)
                Timber.d("@@listener isPlaying = $isPlaying, ${player.playWhenReady}")
//                if (!isPlaying) {
//                    val audioTape = getCurrentAudioTape(player)
//                    if (audioTape != null) {
//                        Timber.d("**listener audiotape = $audioTape")
//                        serviceScope.launch {
//                            _audioTapeRepository.upsertAll(audioTape)
//                        }
//                    }
//                }
            }

            // seek時にもくる
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when (playbackState) {
                    Player.STATE_IDLE -> {
                        // prepare前
                        // 1度prepareしないとこのコールバック来ない
                        // stopなどで停止後に来る
                        // ここに来たらprepareが必要
                        Timber.d("state idle")
                        //repository.updateReadyOk(false)
                        player.prepare()
                    }

                    Player.STATE_BUFFERING -> {
                        Timber.d("@@state buffering position = ${player.contentPosition}")
                        // 再生可能にする準備中
                        //repository.updateReadyOk(false)
                    }

                    Player.STATE_READY -> {
                        // 再生可能
                        // controllerを操作できるようになる
                        // playWhenReady:trueなら再生中 falseなら停止中
                        // seek操作を反映するために必要
                        //_playbackRepository.updateContentPosition(player.contentPosition)
                        Timber.d("@@state ready position = ${player.contentPosition}")
                        //repository.updateReadyOk(true)
//                        if (!player.playWhenReady) {
//                            val audioTape = getCurrentAudioTape(player)
//                            if (audioTape != null) {
//                                serviceScope.launch {
//                                    _audioTapeRepository.updatePosition(
//                                        audioTape.folderPath,
//                                        audioTape.position
//                                    )
//                                }
//                            }
//                        }
                    }


                    Player.STATE_ENDED -> {
                        Timber.d("state ended")
                        // 終了だが条件がわかっていない
                        // stopしたとき？
                        // 要素が0のときもくる
                        // 要素が0のときにフォアグラウンドサービスを止めるのがいいか
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Timber.d("listener error = $error")
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                mediaMetadata.let {
                    Timber.d("##MediaMetadataChanged title: ${it.title}, artist: ${it.artist}, album: ${it.albumTitle}")
                }
                super.onMediaMetadataChanged(mediaMetadata)

            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                Timber.d("##MediaItemTransition = ${mediaItem?.mediaId} reason = $reason position = ${player.contentPosition}")
                super.onMediaItemTransition(mediaItem, reason)

                when (reason) {
                    // 曲が自動で変わったとき
                    Player.MEDIA_ITEM_TRANSITION_REASON_AUTO -> {
                        //updateCurrentMediaIdPosition(mediaItem)
                    }
                    // Seekで曲が変わったとき
                    Player.MEDIA_ITEM_TRANSITION_REASON_SEEK -> {
                        //updateCurrentMediaIdPosition(mediaItem)
                    }
                    // changedは0番目がcurrentになってしまうので設定場所でCurrentMediaIdの更新を行う
                    else -> {}

                }
            }

            private fun updateCurrentMediaIdPosition(mediaItem: MediaItem?) {
                val mediaId = mediaItem?.mediaId ?: ""
                _playbackRepository.updateCurrentMediaIdPosition(mediaId, player.contentPosition)
            }

            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
//                Timber.d("##listner size = ${events.size()}")
//                for(i in 0 until events.size()){
//                    Timber.d("##listener events = ${events.get(i)}")
//                }
                var tapeFlag = 0    // 1:position 2:current
                var uiFlag = 0  // 1:再生状態 2:position 4:current
                if (events.contains(Player.EVENT_IS_PLAYING_CHANGED)) {
                    // 停止の場合位置を更新する - 再生状態と位置を更新する
                    Timber.d("##EVENT_IS_PLAYING_CHANGED play:${player.isPlaying} ready:${player.playbackState}")
                    if (!player.isPlaying) {
                        tapeFlag = 1
                    }
                    uiFlag = 3
                }
                if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) {
                    // current + 位置を更新する - current + 位置を更新する
                    Timber.d("##EVENT_MEDIA_ITEM_TRANSITION play:${player.isPlaying} current:${player.currentMediaItem?.mediaId}, position:${player.contentPosition}")
                    tapeFlag = tapeFlag or 3
                    uiFlag = uiFlag or 6
                }
                if (events.contains(Player.EVENT_POSITION_DISCONTINUITY)) {
                    // 停止の場合位置を変更する - 位置を変更する
                    Timber.d("##EVENT_POSITION_DISCONTINUITY play:${player.isPlaying} current:${player.currentMediaItem?.mediaId}, position:${player.contentPosition}")
                    if (!player.isPlaying) {
                        tapeFlag = tapeFlag or 1
                    }
                    uiFlag = uiFlag or 2
                }
                if (events.contains(Player.EVENT_MEDIA_METADATA_CHANGED)) {
                    Timber.d("##EVENT_MEDIA_METADATA_CHANGED")
                }

                if (tapeFlag > 0) {
                    val audioTape = playerToAudioTape(player)
                    when {
                        (tapeFlag and 3) == 3 -> {
                            serviceScope.launch {
                                _audioTapeRepository.updateCurrentNamePosition(
                                    audioTape.folderPath,
                                    audioTape.currentName,
                                    audioTape.position
                                )
                            }
                        }

                        else -> {
                            serviceScope.launch {
                                _audioTapeRepository.updatePosition(
                                    audioTape.folderPath,
                                    audioTape.position
                                )
                            }
                        }
                    }
                }
                when {
                    (uiFlag and 7) == 7 -> {
                        _playbackRepository.updateAll(
                            player.isPlaying,
                            player.currentMediaItem?.mediaId ?: "",
                            player.contentPosition
                        )
                    }

                    (uiFlag and 6) == 6 -> {
                        _playbackRepository.updateCurrentMediaIdPosition(
                            player.currentMediaItem?.mediaId ?: "",
                            player.contentPosition
                        )
                    }

                    (uiFlag and 3) == 3 -> {
                        _playbackRepository.updatePlayingPosition(
                            player.isPlaying,
                            player.contentPosition
                        )
                    }

                    (uiFlag and 2) == 2 -> {
                        _playbackRepository.updateContentPosition(player.contentPosition)
                    }

                    (uiFlag and 1) == 1 -> {
                        _playbackRepository.updatePlaying(player.isPlaying)
                    }
                }
            }
        })
    }
    /*
        @UnstableApi
        private inner class MediaSessionCallback : MediaSession.Callback {
            override fun onConnect(
                session: MediaSession,
                controller: MediaSession.ControllerInfo
            ): MediaSession.ConnectionResult {
                Timber.d("**onConnect session = $session controller = $controller")
                if (session.isMediaNotificationController(controller)) {
                    // 通知バーが必要な時だけくるはず
                    Timber.d("**isMediaNotificationController")
                }
                return super.onConnect(session, controller)
            }

            override fun onDisconnected(
                session: MediaSession,
                controller: MediaSession.ControllerInfo
            ) {
                super.onDisconnected(session, controller)
                Timber.d("**onDisconnected session = $session controller = $controller")
            }

            override fun onMediaButtonEvent(
                session: MediaSession,
                controllerInfo: MediaSession.ControllerInfo,
                intent: Intent
            ): Boolean {
                Timber.d("**onMediaButtonEvent controllerInfo: $controllerInfo intent: $intent")
                return super.onMediaButtonEvent(session, controllerInfo, intent)
            }

            @OptIn(DelicateCoroutinesApi::class)
            override fun onPlaybackResumption(
                mediaSession: MediaSession,
                controller: MediaSession.ControllerInfo
            ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
                Timber.d("**onPlaybackResumption")
                val settable = SettableFuture.create<MediaSession.MediaItemsWithStartPosition>()
                serviceScope.launch {
                    // Your app is responsible for storing the playlist, metadata (like title
                    // and artwork) of the current item and the start position to use here.
                    //val resumptionPlaylist = restorePlaylist()
                    //settable.set(resumptionPlaylist)
                    settable.set(
                        MediaSession.MediaItemsWithStartPosition(
                            emptyList(),
                            0,
                            0L
                        )
                    )
                }
                return settable
            }
        }*/
}