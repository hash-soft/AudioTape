package com.hashsoft.audiotape.ui

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.service.PlaybackService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.io.File


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

    fun setPlaybackParameters(speed: Float, pitch: Float) {
        _controller?.playbackParameters = PlaybackParameters(speed, pitch)
    }

    fun setSpeed(speed: Float) {
        _controller?.run {
            playbackParameters = PlaybackParameters(speed, playbackParameters.pitch)
        }
    }

    fun setPitch(pitch: Float) {
        _controller?.run {
            playbackParameters = PlaybackParameters(playbackParameters.speed, pitch)
        }
    }

    fun setVolume(volume: Float) {
        _controller?.volume = volume
    }

    fun setRepeat(repeat: Boolean) {
        _controller?.repeatMode =
            if (repeat) MediaController.REPEAT_MODE_ALL else MediaController.REPEAT_MODE_OFF
    }

    fun getContentPosition(): Long {
        return _controller?.contentPosition ?: 0L
    }

    fun seekTo(position: Long) {
        if (position < 0) {
            Timber.e("seekTo position is negative $position")
            return
        }
        _controller?.seekTo(position)
    }

    fun seekToNext() {
        // リピートじゃない場合最後だと先頭に戻らないので無理やり先頭に戻している
        _controller?.run {
            if (mediaItemCount == 0) {
                return@run
            }
            if (nextMediaItemIndex < 0) {
                seekTo(0, 0)
            } else {
                seekToNext()
            }
        }
    }

    fun seekToPrevious() {
        // リピートじゃない場合最初だと最後に戻らないので無理やり最後に戻している
        // previousMediaItemIndexだけで判断すると途中の位置でも最後に戻ってしまうので2秒以内の再生位置なら最後に戻す
        _controller?.run {
            if (mediaItemCount == 0) {
                return@run
            }
            if (previousMediaItemIndex < 0) {
                if (currentPosition < 2000) {
                    seekTo(mediaItemCount - 1, 0)
                } else {
                    seekToPrevious()
                }
            } else {
                seekToPrevious()
            }
        }
    }

    fun setMediaItems(
        audioList: List<AudioItemDto>,
        mediaItemIndex: Int = 0,
        positionMs: Long = 0
    ) {
        if (audioList.isEmpty()) {
            return
        }
        // audio判定されたファイルはmetadata取得済みなので設定する
        val mediaItems = audioList.map { audio ->
            val builder = MediaItem.Builder().setUri(audio.absolutePath + File.separator + audio.name)
                .setMediaId(audio.id.toString())
            val metadata = audio.metadata
            val mediaMetadata = MediaMetadata.Builder()
                .setArtist(metadata.artist)
                .setTitle(audio.name)
                .setDurationMs(metadata.duration)
                .setAlbumTitle(metadata.album)
                .build()
            builder.setMediaMetadata(mediaMetadata)
            builder.build()
        }

        // 初期位置も合わせて設定する
        _controller?.let {
            it.setMediaItems(mediaItems, mediaItemIndex, positionMs)
            it.prepare()
        }
    }

    fun isCurrentByPath(path: String): Boolean {
        val controller = _controller ?: return false
        val currentPath = controller.currentMediaItem?.localConfiguration?.uri?.path ?: ""
        return currentPath == path
    }

    fun includeMediaItemByPath(path: String): Boolean {
        val controller = _controller ?: return false
        val count = controller.mediaItemCount
        for (i in 0 until count) {
            val item = controller.getMediaItemAt(i)
            val itemPath = item.localConfiguration?.uri?.path ?: ""
            if (itemPath == path) {
                return true
            }
        }
        return false
    }

    fun seekToByPath(path: String, position: Long): Boolean {
        val controller = _controller ?: return false
        val count = controller.mediaItemCount
        for (i in 0 until count) {
            val item = controller.getMediaItemAt(i)
            val itemPath = item.localConfiguration?.uri?.path ?: ""
            if (itemPath == path) {
                controller.seekTo(i, position)
                return true
            }
        }
        return false
    }
}
