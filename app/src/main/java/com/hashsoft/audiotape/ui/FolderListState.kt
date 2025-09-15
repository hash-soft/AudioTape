package com.hashsoft.audiotape.ui

import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.DisplayStorageItem
import com.hashsoft.audiotape.data.PlaybackDto
import com.hashsoft.audiotape.data.StorageItemDto
import com.hashsoft.audiotape.data.StorageItemListRepository
import com.hashsoft.audiotape.data.StorageItemMetadata
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FolderListState(
    private val _storageItemListRepository: StorageItemListRepository,
    private val _list: MutableStateFlow<List<DisplayStorageItem>> = MutableStateFlow(
        emptyList()
    ),
) {
    private val _storageCache: MutableList<StorageItemDto> = mutableListOf()
    val list: StateFlow<List<DisplayStorageItem>> = _list.asStateFlow()

    fun loadStorageCache(path: String) {
        _storageCache.clear()
        _storageCache.addAll(_storageItemListRepository.pathToStorageItemList(path))
    }

    fun updateList(audioTape: AudioTapeDto, playback: PlaybackDto, playingFolderPath: String) {
        StorageItemListRepository.sort(_storageCache, audioTape.sortOrder)
        val isCurrent = audioTape.folderPath == playingFolderPath
        var folderCount = 0
        var fileCount = 0
        val list = _storageCache.map {
            val index = when (it.metadata) {
                is StorageItemMetadata.Folder -> folderCount++
                else -> fileCount++
            }
            makeDisplayStorageItem(isCurrent, it, audioTape, playback, index)
        }
        _list.update { list }
    }

    private fun makeDisplayStorageItem(
        isCurrent: Boolean,
        item: StorageItemDto,
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

    fun loadMetadataByIndex(index: Int) {
        val item = _list.value.getOrNull(index)
        // 未解析ファイル以外はスキップ
        if (item == null || item.base.metadata !is StorageItemMetadata.UnanalyzedFile) return
        val metadata = _storageItemListRepository.loadMetadata(item.base.path)
        val itemMetadata =
            if (metadata == null) StorageItemMetadata.InvalidFile else StorageItemMetadata.Audio(
                metadata
            )
        _storageCache[index] = item.base.copy(metadata = itemMetadata)
        _list.update { list ->
            list.toMutableList().apply {
                this[index] = item.copy(_storageCache[index])
            }
        }
    }

}



