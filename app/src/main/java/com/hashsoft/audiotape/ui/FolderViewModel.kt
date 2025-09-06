package com.hashsoft.audiotape.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.FolderStateRepository
import com.hashsoft.audiotape.data.StorageAddressRepository
import com.hashsoft.audiotape.data.StorageItemListRepository
import com.hashsoft.audiotape.data.StorageItemMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class FolderViewState {
    Start,
    ItemLoading,
    Success,
}

class FolderViewModel(
    private val _folderStateRepository: FolderStateRepository,
    storageAddressRepository: StorageAddressRepository,
    storageItemListRepository: StorageItemListRepository,
    private val _audioTapeRepository: AudioTapeRepository,
    private val _controller: AudioController
) :
    ViewModel() {

    private val _state = MutableStateFlow(FolderViewState.Start)
    val state: StateFlow<FolderViewState> = _state.asStateFlow()
    private val _selectedPath = MutableStateFlow("")
    val selectedPath: StateFlow<String> = _selectedPath.asStateFlow()

    val addressBarState = AddressBarState(storageAddressRepository)
    val folderListState = FolderListState(storageItemListRepository)

    init {
        viewModelScope.launch {
            @OptIn(ExperimentalCoroutinesApi::class)
            _folderStateRepository.folderStateFlow().flatMapLatest { folderState ->
                addressBarState.load(folderState.selectedPath)
                _selectedPath.update { folderState.selectedPath }
                _state.update { FolderViewState.ItemLoading }
                _audioTapeRepository.findByPath(folderState.selectedPath)
                    .map { audioTape -> folderState to audioTape }
            }.collect() { (folderState, audioTape) ->
                folderListState.loadList(folderState.selectedPath, audioTape)
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

    fun setMediaItemsInFolderList(
        index: Int = 0,
        positionMs: Long = 0
    ) {
        // ファイルを抽出する
        val audioList = folderListState.list.value.mapNotNull {
            when (it.metadata) {
                is StorageItemMetadata.Folder -> {
                    return@mapNotNull null
                }

                else -> it
            }
        }
        // ファイルインデックスに変換
        val mediaItemIndex = folderListState.typeIndexList.value[index]
        _controller.setMediaItems(audioList, mediaItemIndex, positionMs)
        _controller.play()
    }

}



