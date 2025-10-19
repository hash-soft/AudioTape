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
    private val _list: MutableStateFlow<List<DisplayStorageItem>> = MutableStateFlow(
        emptyList()
    ),
) {
    private val _storageCache: MutableList<StorageItem> = mutableListOf()
    val list: StateFlow<List<DisplayStorageItem>> = _list.asStateFlow()

    fun loadStorageCache(path: String, volumes: List<VolumeItem>) {
        _storageCache.clear()
        _storageCache.addAll(_storageItemListUseCase.pathToStorageItemList(path, volumes, AudioTapeSortOrder.ASIS))
    }

    fun updateList(audioTape: AudioTapeDto, playback: PlaybackDto, playingFolderPath: String) {
        // Todo フォルダとオーディオがまざってしまうのでやるなら別々に取得しておく必要がある、キャッシュしておく必要もなさそうなのでやめるか
        StorageItemListUseCase.sortStorageList(_storageCache, audioTape.sortOrder)
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
    ): DisplayStorageItem {
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



