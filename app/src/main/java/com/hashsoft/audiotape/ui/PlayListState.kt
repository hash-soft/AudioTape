package com.hashsoft.audiotape.ui

import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeSortOrder
import com.hashsoft.audiotape.data.DisplayStorageItem
import com.hashsoft.audiotape.data.StorageItem
import com.hashsoft.audiotape.data.StorageItemListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PlayListState(
    private val _storageItemListUseCase: StorageItemListUseCase,
    private val _list: MutableStateFlow<List<DisplayStorageItem>> = MutableStateFlow(
        emptyList()
    ),
) {
    private val _storageCache: MutableList<AudioItemDto> = mutableListOf()
    val list: StateFlow<List<DisplayStorageItem>> = _list.asStateFlow()

    fun loadStorageCache(path: String) {
        _storageCache.clear()
        _storageCache.addAll(
            _storageItemListUseCase.getAudioItemList(
                path, AudioTapeSortOrder.ASIS
            )
        )
    }

    fun updateList(audioTape: AudioTapeDto) {
        StorageItemListUseCase.sortAudioList(_storageCache, audioTape.sortOrder)
        val list = _storageCache.mapIndexed { index, item ->
            makeDisplayStorageItem(item, audioTape, index)
        }
        _list.update { list }
    }

    private fun makeDisplayStorageItem(
        item: StorageItem,
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
