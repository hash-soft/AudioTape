package com.hashsoft.audiotape.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.FolderStateRepository
import com.hashsoft.audiotape.data.PlaybackRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.ResumeAudioRepository
import com.hashsoft.audiotape.data.StorageAddressRepository
import com.hashsoft.audiotape.data.StorageItemListRepository
import com.hashsoft.audiotape.data.StorageItemMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

enum class FolderViewState {
    Start,
    ItemLoading,
    Success,
}

class FolderViewModel(
    private val _controller: AudioController = AudioController(),
    private val _folderStateRepository: FolderStateRepository,
    storageAddressRepository: StorageAddressRepository,
    storageItemListRepository: StorageItemListRepository,
    private val _audioTapeRepository: AudioTapeRepository,
    private val _playingStateRepository: PlayingStateRepository,
    private val _playbackRepository: PlaybackRepository,
    resumeAudioRepository: ResumeAudioRepository
) :
    ViewModel() {

    val isReady = _controller.isReady
    private val _state = MutableStateFlow(FolderViewState.Start)
    val state: StateFlow<FolderViewState> = _state.asStateFlow()
    private val _selectedPath = MutableStateFlow("")
    val selectedPath: StateFlow<String> = _selectedPath.asStateFlow()

    val addressBarState = AddressBarState(storageAddressRepository)
    val folderListState = FolderListState(storageItemListRepository)
    val playItemState = PlayItemState(
        viewModelScope,
        _playbackRepository,
        _audioTapeRepository,
        _playingStateRepository,
        resumeAudioRepository
    )

    init {
        viewModelScope.launch {
            @OptIn(ExperimentalCoroutinesApi::class)
            _folderStateRepository.folderStateFlow().flatMapLatest { folderState ->
                addressBarState.load(folderState.selectedPath)
                _selectedPath.update { folderState.selectedPath }
                _state.update { FolderViewState.ItemLoading }
                // collect後にloadするとアドレス不変で再取得されるのでキャッシュしておく
                folderListState.loadStorageCache(folderState.selectedPath)
                combine(
                    _audioTapeRepository.findByPath(folderState.selectedPath),
                    _playbackRepository.data,
                    _playingStateRepository.playingStateFlow()
                ) { audioTape, playback, playingState ->
                    Triple(audioTape, playback, playingState)
                }
            }.collect() { (audioTape, playback, playingState) ->
                folderListState.updateList(audioTape, playback, playingState.folderPath)
                _state.update { FolderViewState.Success }
            }
        }
    }

    fun loadMetadata(index: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            folderListState.loadMetadataByIndex(index)
        }
    }

    fun saveSelectedPath(path: String) = viewModelScope.launch {
        _folderStateRepository.saveSelectedPath(path)
    }

    fun updatePlayingFolderPath(path: String) = viewModelScope.launch {
        _playingStateRepository.saveFolderPath(path)
    }

    fun setMediaItemsInFolderList(index: Int = 0) {
        // ファイルを抽出する
        val audioList = folderListState.list.value.mapNotNull {
            when (it.base.metadata) {
                is StorageItemMetadata.Folder -> {
                    return@mapNotNull null
                }

                else -> it.base
            }
        }
        val item = folderListState.list.value[index]
        if (_controller.isCurrentByPath(item.base.path)) {
            return
        }
        if (_controller.seekToByPath(item.base.path, item.contentPosition)) {
            return
        }
        Timber.d("##includeMediaItemByPath")
        _controller.setMediaItems(audioList, item.index, item.contentPosition)
        Timber.d("##includeMediaItemByPath end")
    }

    fun buildController(context: Context) = _controller.buildController(context)

    fun releaseController() = _controller.releaseController()

    fun play() = _controller.play()

    fun pause() = _controller.pause()

    fun getContentPosition() = _controller.getContentPosition()

    fun seekTo(position: Long) {
        if (_controller.isCurrentMediaItem()) {
            _controller.seekTo(position)
        } else {
            val value = playItemState.item.value
            if (value == null) {
                return
            }
            val file = File(value.path)
            _playbackRepository.updateAll(
                value.isReadyOk,
                value.isPlaying,
                file.name,
                file.parent ?: "",
                value.durationMs,
                position
            )
        }
    }

}



