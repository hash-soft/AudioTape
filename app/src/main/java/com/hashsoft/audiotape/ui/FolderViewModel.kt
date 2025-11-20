package com.hashsoft.audiotape.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.AudioTapeSortOrder
import com.hashsoft.audiotape.data.AudioTapeStagingRepository
import com.hashsoft.audiotape.data.ControllerStateRepository
import com.hashsoft.audiotape.data.DisplayFolder
import com.hashsoft.audiotape.data.FolderStateRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.StorageAddressUseCase
import com.hashsoft.audiotape.data.StorageItem
import com.hashsoft.audiotape.data.StorageItemListUseCase
import com.hashsoft.audiotape.data.StorageItemListUseCase.Companion.sortedAudioList
import com.hashsoft.audiotape.data.StorageItemListUseCase.Companion.sortedFolderList
import com.hashsoft.audiotape.data.StorageVolumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
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
    private val _controllerStateRepository: ControllerStateRepository,
    private val _audioTapeStagingRepository: AudioTapeStagingRepository,
    storageVolumeRepository: StorageVolumeRepository,
    audioStoreRepository: AudioStoreRepository
) :
    ViewModel() {

    private val _state = MutableStateFlow(FolderViewState.Start)
    val state: StateFlow<FolderViewState> = _state.asStateFlow()

    val addressBarState = AddressBarState(storageAddressUseCase)


    private val _baseState = combine(
        storageVolumeRepository.volumeChangeFlow(),
        audioStoreRepository.updateFlow,
        _folderStateRepository.folderStateFlow()
    ) { volumes, _, folderState ->
        _state.update { FolderViewState.ItemLoading }
        addressBarState.load(folderState.selectedPath, volumes)
        val listPair =
            storageItemListUseCase.pathToStorageItemList(volumes, folderState.selectedPath, null)
        folderState.selectedPath to listPair
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _listState = _baseState.flatMapLatest { pair ->
        combine(
            _audioTapeRepository.findSortOrderByPath(pair.first)
        ) { (tapeSortOrder) ->
            val listPair = pair.second
            val sortOrder = tapeSortOrder ?: AudioTapeSortOrder.NAME_ASC
            val folderList = sortedFolderList(listPair.first, sortOrder)
            val audioList = sortedAudioList(listPair.second, sortOrder)
            val folderSize = listPair.first.size
            val expandIndexList =
                folderList.indices.toList() + audioList.indices.map { it + folderSize }
            pair.first to Pair(folderList + audioList, expandIndexList)

        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val displayFolderState = _listState.flatMapLatest { (folderPath, listPair) ->
        combine(
            _audioTapeRepository.findByPath(folderPath),
            _controllerStateRepository.data,
            _playingStateRepository.playingStateFlow()
        ) { audioTape, controllerState, playingState ->
            _state.update { FolderViewState.Success }
            DisplayFolder(
                folderPath = folderPath,
                listPair.first,
                listPair.second,
                audioTape,
                playingState,
                controllerState
            )
        }
    }.stateIn(
        viewModelScope, SharingStarted.Eagerly, DisplayFolder("")
    )

    fun saveSelectedPath(path: String) = viewModelScope.launch {
        _folderStateRepository.saveSelectedPath(path)
    }

    fun updatePlayingFolderPath(path: String) = viewModelScope.launch {
        _playingStateRepository.saveFolderPath(path)
    }

    fun setMediaItemsInFolderList(list: List<StorageItem>, index: Int, position: Long) {
        // ファイルを抽出する
        val audioList = list.mapNotNull {
            when (it) {
                is AudioItemDto -> it

                else -> return@mapNotNull null
            }
        }
        val audioItem = audioList.getOrNull(index) ?: return
        if (_controller.isCurrentById(audioItem.id)) {
            return
        }
        if (_controller.seekToById(audioItem.id, position)) {
            return
        }
        if (_controller.isCurrentMediaItem()) {
            // 位置を更新する
            _audioTapeStagingRepository.updatePosition(_controller.getContentPosition())
        }
        _controller.setMediaItems(audioList, index, position)
    }

    fun setPlayingParameters(audioTape: AudioTapeDto) {
        _controller.setRepeat(audioTape.repeat)
        _controller.setPlaybackParameters(audioTape.speed, audioTape.pitch)
        _controller.setVolume(audioTape.volume)
    }

    fun play() = _controller.play()

    fun createTapeNotExist(folderPath: String, currentName: String, position: Long) {
        viewModelScope.launch {
            val result = _audioTapeRepository.insertNew(
                folderPath, currentName = currentName, position = position,
                sortOrder = AudioTapeSortOrder.NAME_ASC
            )
            Timber.d("insertNew result: $result folderPath: $folderPath")
        }
    }
}



