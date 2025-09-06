package com.hashsoft.audiotape.ui

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.DisplayStorageItemExtra
import com.hashsoft.audiotape.data.PlaybackDto
import com.hashsoft.audiotape.data.PlaybackRepository
import com.hashsoft.audiotape.data.StorageItemDto
import com.hashsoft.audiotape.data.StorageItemMetadata
import com.hashsoft.audiotape.service.PlaybackService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

class SimpleAudioControllerViewModel(
    private val _playbackRepository: PlaybackRepository,
) :
    ViewModel() {

    private lateinit var _controllerFuture: ListenableFuture<MediaController>
    private lateinit var _controller: MediaController

    // 演奏中のパスであって表示中のパスではない
    private var musicPath: String = ""

    private val _controllerState = MutableStateFlow(SimpleAudioControllerState(false))
    val controllerState: StateFlow<SimpleAudioControllerState> = _controllerState.asStateFlow()

    val uiState: StateFlow<SimpleAudioControllerUiState> = _playbackRepository.data.map {
        // musicPathが一致してない場合AudioTapeDto見ればいいから情報いらないな
        // sealed interfaceで分けるか
        SimpleAudioControllerUiState(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SimpleAudioControllerUiState(
            PlaybackDto(
                isReadyOk = false,
                isPlaying = false,
                currentMediaId = "",
                0
            )
        )
    )

    fun buildController(context: Context) {
        _controllerFuture =
            MediaController.Builder(
                context,
                SessionToken(context, ComponentName(context, PlaybackService::class.java))
            )
                .buildAsync()
        _controllerFuture.addListener({
            _controller = _controllerFuture.get()
            _controllerState.value = SimpleAudioControllerState(true)
        }, ContextCompat.getMainExecutor(context))
    }

    fun releaseController() {
        if (_controllerState.value.isReadyOk) {
            _controller.release()
            MediaController.releaseFuture(_controllerFuture)
            _controllerState.value = SimpleAudioControllerState(false)
        }
    }


    fun isPlaying(): Boolean {
        return _controller.isPlaying
    }

    fun pause() {
        _controller.pause()
    }

    fun play() {
        _controller.play()
    }

    fun isPlaybackFolder(musicPath: String): Boolean {
        return this.musicPath == musicPath
    }

    fun isPlaybackContinued(musicPath: String): Boolean {
        return _controller.mediaItemCount != 0 && this.musicPath == musicPath
    }

    fun needUpdateMediaItem(musicPath: String): Boolean {
        // パスが変わったもしくは設定されているMediaItemが0
        Timber.d("musicPath: $musicPath count: ${_controller.mediaItemCount}")
        return _controller.mediaItemCount == 0 || this.musicPath != musicPath
    }


    fun setMediaItems(
        audioList: List<AudioItemDto>,
        mediaItemIndex: Int = 0,
        positionMs: Long = 0
    ): Long? {
        if (audioList.isEmpty()) {
            return null
        }
        val mediaItems = audioList.map { audio ->
            val metadata = audio.metadata
            MediaItem.Builder().setUri(audio.path)
                .setMediaId(audio.path)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setArtist(metadata.artist)
                        .setTitle(audio.name)
                        .setDurationMs(metadata.duration)
                        .build()
                ).build()
        }
        val isPlaying = _controller.isPlaying
        val position = _controller.contentPosition

        // 初期位置も合わせて設定する
        _controller.setMediaItems(mediaItems, mediaItemIndex, positionMs)
        _controller.prepare()

        return if (isPlaying) position else null
    }

    fun mapAudioMetadata(itemList: List<StorageItemDto>): List<AudioItemDto> {
        return itemList.mapNotNull {
            when (it.metadata) {
                is StorageItemMetadata.Audio -> {
                    AudioItemDto(it.name, it.path, it.size, it.lastModified, it.metadata.contents)
                }

                else -> return@mapNotNull null
            }
        }
    }

    fun seekToSelectedAudio(path: String, position: Long): Boolean {
        for (i in 0 until _controller.mediaItemCount) {
            val item = _controller.getMediaItemAt(i)
            if (item.mediaId == path) {
                _controller.seekTo(i, position)
                return true
            }
        }
        return false
    }

    fun getContentPosition(): Long {
        return _controller.contentPosition
    }

    fun getContentDuration(): Long {
        return _controller.contentDuration
    }

    fun seekTo(position: Long) {
        _controller.seekTo(position)
    }

    fun updateCurrentMediaId(mediaId: String) {
        _playbackRepository.updateCurrentMediaId(mediaId)
    }


    fun getDisplayItemExtra(
        mediaId: String,
        path: String,
        list: List<StorageItemDto>,
        audioTape: AudioTapeDto
    ): DisplayStorageItemExtra {
        if (path == musicPath) {
            list.forEachIndexed { index, item ->
                if (item.path == mediaId) {
                    return DisplayStorageItemExtra(
                        index,
                        _controller.isPlaying,
                        _controller.contentPosition
                    )
                }
            }
        } else {
            // 再生中ではないのでaudioTapeから取得する
            list.forEachIndexed { index, item ->
                if (item.name == audioTape.currentName) {
                    return DisplayStorageItemExtra(index, false, audioTape.position)
                }
            }
        }
        // 追加データはないのでインデックスが一致しないようにする
        return DisplayStorageItemExtra(-1, false, 0)
    }


    data class SimpleAudioControllerState(
        val isReadyOk: Boolean
    )

    data class SimpleAudioControllerUiState(
        val data: PlaybackDto
    )
}
