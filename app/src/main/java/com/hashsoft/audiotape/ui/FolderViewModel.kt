package com.hashsoft.audiotape.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.AudioTapeSortOrder
import com.hashsoft.audiotape.data.FolderStateRepository
import com.hashsoft.audiotape.data.PlaybackRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.StorageAddressUseCase
import com.hashsoft.audiotape.data.StorageItemListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject

enum class FolderViewState {
    Start,
    ItemLoading,
    Success,
}

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val _controller: AudioController,
    private val _folderStateRepository: FolderStateRepository,
    storageAddressUseCase: StorageAddressUseCase,
    storageItemListUseCase: StorageItemListUseCase,
    private val _audioTapeRepository: AudioTapeRepository,
    private val _playingStateRepository: PlayingStateRepository,
    private val _playbackRepository: PlaybackRepository,
    private val _storageItemListUseCase: StorageItemListUseCase
) :
    ViewModel() {

    private val _state = MutableStateFlow(FolderViewState.Start)
    val state: StateFlow<FolderViewState> = _state.asStateFlow()
    private val _selectedPath = MutableStateFlow("")
    val selectedPath: StateFlow<String> = _selectedPath.asStateFlow()

    val addressBarState = AddressBarState(storageAddressUseCase)
    val folderListState = FolderListState(storageItemListUseCase)
    private var _audioTape: AudioTapeDto = AudioTapeDto("", "")


    init {
        viewModelScope.launch {
            @OptIn(ExperimentalCoroutinesApi::class)
            _folderStateRepository.folderStateFlow().flatMapLatest { folderState ->
                addressBarState.load(folderState.selectedPath)
                _selectedPath.update { folderState.selectedPath }
                _state.update { FolderViewState.ItemLoading }
                // collect後にloadするとアドレス不変で再取得されるのでキャッシュしておく
                folderListState.loadStorageCache(folderState.selectedPath)
                val result = _storageItemListUseCase.pathToStorageItemList(
                    folderState.selectedPath,
                    AudioTapeSortOrder.ASIS
                )
                combine(
                    _audioTapeRepository.findByPath(folderState.selectedPath),
                    _playbackRepository.data,
                    _playingStateRepository.playingStateFlow()
                ) { audioTape, playback, playingState ->
                    Triple(audioTape, playback, playingState)
                }
            }.collect { (audioTape, playback, playingState) ->
                folderListState.updateList(audioTape, playback, playingState.folderPath)
                _state.update { FolderViewState.Success }
                _audioTape = audioTape
            }
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
            when (it.base) {
                is AudioItemDto -> {
                    it.base
                }

                else -> return@mapNotNull null
            }
        }
        val item = folderListState.list.value[index]
        val fullPath = item.base.absolutePath + File.separator + item.base.name
        if (_controller.isCurrentByPath(fullPath)) {
            return
        }
        if (_controller.seekToByPath(fullPath, item.contentPosition)) {
            return
        }
        if (_controller.isCurrentMediaItem()) {
            // 位置を更新する
            _playbackRepository.updateContentPosition(_controller.getContentPosition())
        }
        _controller.setMediaItems(audioList, item.index, item.contentPosition)
    }

    fun setPlayingParameters() {
        _controller.setRepeat(_audioTape.repeat)
        _controller.setPlaybackParameters(_audioTape.speed, _audioTape.pitch)
        _controller.setVolume(_audioTape.volume)
    }

    fun play() = _controller.play()

    fun createTapeNotExist(folderPath: String, index: Int) {
        val item = folderListState.list.value[index]
        viewModelScope.launch {
            val result = _audioTapeRepository.insertNew(
                folderPath, item.base.name, position = item.contentPosition,
                sortOrder = AudioTapeSortOrder.NAME_ASC
            )
            Timber.d("insertNew result: $result folderPath: $folderPath")
        }
    }
}



