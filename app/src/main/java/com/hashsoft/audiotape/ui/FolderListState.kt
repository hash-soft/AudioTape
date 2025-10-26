package com.hashsoft.audiotape.ui

import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeSortOrder
import com.hashsoft.audiotape.data.DisplayStorageItem
import com.hashsoft.audiotape.data.FolderItemDto
import com.hashsoft.audiotape.data.PlaybackDto
import com.hashsoft.audiotape.data.StorageItem
import com.hashsoft.audiotape.data.StorageItemListUseCase
import com.hashsoft.audiotape.data.VolumeItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FolderListState(
    private val _storageItemListUseCase: StorageItemListUseCase,
    private val _list: MutableStateFlow<List<DisplayStorageItem<StorageItem>>> = MutableStateFlow(
        emptyList()
    ),
) {
    private val _storageCache: MutableList<StorageItem> = mutableListOf()
    private var _lastSortOrder: AudioTapeSortOrder = AudioTapeSortOrder.ASIS

    val list: StateFlow<List<DisplayStorageItem<StorageItem>>> = _list.asStateFlow()

    fun loadStorageCache(path: String, volumes: List<VolumeItem>) {
        _storageCache.clear()
        // キャッシュ取得時点ではソートしない
        _storageCache.addAll(
            _storageItemListUseCase.pathToStorageItemList(
                volumes,
                path,
                AudioTapeSortOrder.ASIS
            )
        )
        _lastSortOrder = AudioTapeSortOrder.ASIS
    }

    fun updateList(audioTape: AudioTapeDto, playback: PlaybackDto, playingFolderPath: String) {
        if (_lastSortOrder != audioTape.sortOrder) {
            StorageItemListUseCase.sortFoldersAndAudiosSeparately(
                _storageCache,
                audioTape.sortOrder
            )
            _lastSortOrder = audioTape.sortOrder
        }
        val isCurrent = audioTape.folderPath == playingFolderPath
        var folderCount = 0
        var fileCount = 0
        val list = _storageCache.map {
            val index = when (it) {
                is FolderItemDto -> folderCount++
                else -> fileCount++
            }
            makeDisplayStorageItem(isCurrent, it, audioTape, playback, index)
        }
        _list.update { list }
    }

    private fun makeDisplayStorageItem(
        isCurrent: Boolean,
        item: StorageItem,
        audioTape: AudioTapeDto,
        playback: PlaybackDto,
        index: Int
    ): DisplayStorageItem<StorageItem> {
        val isTarget = item.name == audioTape.currentName
        val contentPosition = if (isTarget) audioTape.position else 0
        val isResume = !isCurrent && isTarget
        val color = when {
            isResume -> 2
            isTarget -> 1
            else -> 0
        }
        val icon = if (isCurrent && isTarget && playback.isPlaying) 1 else 0
        return DisplayStorageItem(item, index, color, icon, isResume, contentPosition)
    }

}



