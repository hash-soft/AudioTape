package com.hashsoft.audiotape.ui

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.hashsoft.audiotape.data.StorageItemDto
import com.hashsoft.audiotape.data.StorageItemMetadata
import com.hashsoft.audiotape.service.PlaybackService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class AudioController(
    private val _isReady: MutableStateFlow<Boolean> = MutableStateFlow(false)
) {

    private var _controllerFuture: ListenableFuture<MediaController>? = null
    private var _controller: MediaController? = null

    val isReady = _isReady.asStateFlow()

    fun buildController(context: Context) {
        val sessionToken =
            SessionToken(context, ComponentName(context, PlaybackService::class.java))
        _controllerFuture = MediaController.Builder(context, sessionToken).buildAsync().also {
            it.addListener({
                _controller = it.get()
                _isReady.update { true }
            }, ContextCompat.getMainExecutor(context))
        }
    }

    fun releaseController() {
        _controller?.let {
            it.release()
            _controller = null
        }
        _controllerFuture?.let {
            MediaController.releaseFuture(it)
            _controllerFuture = null
        }
        _isReady.update { false }
    }

    fun isCurrentMediaItem(): Boolean {
        return _controller?.currentMediaItem != null
    }

    fun play() {
        _controller?.play()
    }

    fun pause() {
        _controller?.pause()
    }

    fun getContentPosition(): Long {
        return _controller?.contentPosition ?: 0L
    }

    fun seekTo(position: Long) {
        _controller?.seekTo(position)
    }

    fun setMediaItems(
        audioList: List<StorageItemDto>,
        mediaItemIndex: Int = 0,
        positionMs: Long = 0
    ) {
        if (audioList.isEmpty()) {
            return
        }
        // audio判定されたファイルはmetadata取得済みなので設定する
        val mediaItems = audioList.map { audio ->
            val builder = MediaItem.Builder().setUri(audio.path)
                .setMediaId(audio.name)
            val mediaMetadata = when (audio.metadata) {
                is StorageItemMetadata.Audio -> {
                    val metadata = audio.metadata.contents
                    MediaMetadata.Builder()
                        .setArtist(metadata.artist)
                        .setTitle(audio.name)
                        .setDurationMs(metadata.duration)
                        .setAlbumTitle(metadata.album)
                        .build()
                }

                else -> null
            }
            if (mediaMetadata != null) {
                builder.setMediaMetadata(mediaMetadata)
            }
            builder.build()
        }

        // 初期位置も合わせて設定する
        _controller?.let {
            it.setMediaItems(mediaItems, mediaItemIndex, positionMs)
            it.prepare()
        }
    }
}