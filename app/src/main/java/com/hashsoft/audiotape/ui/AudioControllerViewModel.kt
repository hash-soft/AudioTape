package com.hashsoft.audiotape.ui

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
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
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.DisplayStorageItemExtra
import com.hashsoft.audiotape.data.PlaybackDto
import com.hashsoft.audiotape.data.PlaybackRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.StorageItemDto
import com.hashsoft.audiotape.data.StorageItemListRepository
import com.hashsoft.audiotape.data.StorageItemMetadata
import com.hashsoft.audiotape.service.PlaybackService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

class AudioControllerViewModel(
    context: Context,
    private val _playbackRepository: PlaybackRepository,
    private val _playingStateRepository: PlayingStateRepository,
    private val _storageItemListRepository: StorageItemListRepository,
    private val _audioTapeRepository: AudioTapeRepository
) :
    ViewModel() {

    private var _controllerFuture: ListenableFuture<MediaController>
    private lateinit var _controller: MediaController

    // 演奏中のパスであって表示中のパスではない
    private var musicPath: String = ""

    val isUsable: MutableState<Boolean> = mutableStateOf(false)

    val uiState: StateFlow<AudioControllerUiState> = _playbackRepository.data.map {
        // musicPathが一致してない場合AudioTapeDto見ればいいから情報いらないな
        // sealed interfaceで分けるか
        AudioControllerUiState(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AudioControllerUiState(
            PlaybackDto(
                isReadyOk = false,
                isPlaying = false,
                currentMediaId = "",
                0
            )
        )
    )

    init {
        val sessionToken =
            SessionToken(context, ComponentName(context, PlaybackService::class.java))
        _controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        _controllerFuture.addListener({
            _controller = _controllerFuture.get()
            viewModelScope.launch {
                val state = _playingStateRepository.getPlayingState()
                musicPath = state.folderPath
                val audioTape = _audioTapeRepository.findByPath(musicPath).first()
                when {
                    // controllerにmediaItemが残っているときは再生位置を設定するだけ
                    _controller.mediaItemCount != 0 -> {
                        _playbackRepository.updateContentPosition(_controller.contentPosition)
                    }

                    _audioTapeRepository.validAudioTapeDto(audioTape) -> {
                        val storageItemList =
                            _storageItemListRepository.pathToStorageItemList(musicPath)
                        initMediaItems(audioTape, storageItemList)
                    }
                }
                isUsable.value = true
            }
        }, ContextCompat.getMainExecutor(context))

    }

    private fun initMediaItems(audioTape: AudioTapeDto, storageItemList: List<StorageItemDto>) {
        val sortList = StorageItemListRepository.sort(storageItemList, audioTape.sortOrder)
        val audioItemList = mapAudioMetadata(sortList)
        val path = audioTape.folderPath + File.separator + audioTape.currentName
        val mediaItemIndex = audioItemList.indexOfFirst { it.path == path }
        if (mediaItemIndex < 0) {
            setMediaItems(audioItemList)
        } else {
            setMediaItems(audioItemList, mediaItemIndex, audioTape.position)
            _playbackRepository.updateCurrentMediaId(path)
        }
    }

    override fun onCleared() {
        if (isUsable.value) {
            _controller.release()
            MediaController.releaseFuture(_controllerFuture)
        }
        super.onCleared()
    }

    fun getController(): MediaController {
        return _controller
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

    fun updateMusicPath(path: String) {
        musicPath = path
        viewModelScope.launch {
            _playingStateRepository.saveFolderPath(path)
        }
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

    fun updateAudioTapeAll(audioTape: AudioTapeDto) {
        viewModelScope.launch {
            _audioTapeRepository.upsertAll(audioTape)
        }
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

    fun onAudioSelected(
        selectedPath: String,
        storageItemList: List<StorageItemDto>,
        argument: AudioCallbackArgument.AudioSelected
    ) {
        val audioId = selectedPath + File.separator + argument.name
        // 選択したのと同じならplay継続かpause->play
        if (audioId == _controller.currentMediaItem?.mediaId) {
            updateAudioTapeAll(
                AudioTapeDto(
                    musicPath,
                    argument.name,
                    getContentPosition()
                )
            )
            _controller.play()
            return
        }

        val lastPath = musicPath
        val musicPath = selectedPath
        if (needUpdateMediaItem(musicPath)) {
            updateMusicPath(musicPath)
            val audioItemList = mapAudioMetadata(storageItemList)
            val result = setMediaItems(audioItemList)
            // setMediaItemsすると停止になるが同時にmediaItemも設定後のものになるので
            // setMediaItems前に保存する必要がある
            if (result != null) {
                Timber.d("##setMediaItems: $result")
                viewModelScope.launch {
                    _audioTapeRepository.updatePosition(lastPath, result)
                }
            }
        }
        val position = if (argument.isCurrent) argument.position else 0
        if (!seekToSelectedAudio(audioId, position)) {
            Timber.e("seekToSelectedAudio failed")
            return
        }
        // 再生時のAudioTape更新はUI側からだけ
        // 新しく再生するオーディオ情報を更新する
        // 以前のオーディオは停止時にサービスで更新している
        updateAudioTapeAll(
            AudioTapeDto(
                musicPath,
                argument.name,
                getContentPosition()
            )
        )
        // アプリUIからのplayなので明示的に設定する
        updateCurrentMediaId(audioId)
        Timber.d("play")
        play()
    }

    data class AudioControllerUiState(
        val data: PlaybackDto
    )
}

val LocalAudioControllerViewModel =
    compositionLocalOf<AudioControllerViewModel> { error("AudioControllerViewModel not found !") }
