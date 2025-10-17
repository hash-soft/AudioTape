package com.hashsoft.audiotape.ui

import com.hashsoft.audiotape.data.AudioItemListRepository
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeSortOrder
import com.hashsoft.audiotape.data.DisplayStorageItem
import com.hashsoft.audiotape.data.StorageItemDto
import com.hashsoft.audiotape.data.StorageItemListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PlayListState(
    private val _storageItemListRepository: StorageItemListRepository,
    private val _audioItemListRepository: AudioItemListRepository,
    private val _list: MutableStateFlow<List<DisplayStorageItem>> = MutableStateFlow(
        emptyList()
    ),
) {
    private val _storageCache: MutableList<StorageItemDto> = mutableListOf()
    val list: StateFlow<List<DisplayStorageItem>> = _list.asStateFlow()

    fun loadStorageCache(path: String) {
        _storageCache.clear()
        _storageCache.addAll(_audioItemListRepository.getAudioItemListFromMediaStore(path,
            AudioTapeSortOrder.NAME_ASC
        ))
    }

    fun updateList(audioTape: AudioTapeDto) {
        StorageItemListRepository.sort(_storageCache, audioTape.sortOrder)
        val list = _storageCache.mapIndexed { index, item ->
            makeDisplayStorageItem(item, audioTape, index)
        }
        _list.update { list }
    }

    private fun makeDisplayStorageItem(
        item: StorageItemDto,
        audioTape: AudioTapeDto,
        index: Int
    ): DisplayStorageItem {
        val isTarget = item.name == audioTape.currentName
        val contentPosition = if (isTarget) audioTape.position else 0
        val color = when {
            isTarget -> 1
            else -> 0
        }
        return DisplayStorageItem(item, index, color, 0, false, contentPosition)
    }
}
