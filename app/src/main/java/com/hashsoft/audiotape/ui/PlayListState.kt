package com.hashsoft.audiotape.ui

import com.hashsoft.audiotape.data.AudioItemListRepository
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.DisplayStorageItem
import com.hashsoft.audiotape.data.StorageItemDto
import com.hashsoft.audiotape.data.StorageItemListRepository
import com.hashsoft.audiotape.data.StorageItemMetadata
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PlayListState(
    private val _storageItemListRepository: StorageItemListRepository,
    private val _list: MutableStateFlow<List<DisplayStorageItem>> = MutableStateFlow(
        emptyList()
    ),
) {
    private val _storageCache: MutableList<StorageItemDto> = mutableListOf()
    val list: StateFlow<List<DisplayStorageItem>> = _list.asStateFlow()

    fun loadStorageCache(path: String) {
        _storageCache.clear()
        _storageCache.addAll(AudioItemListRepository(path).getAudioItemList())
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

    fun loadMetadata() {
        for ((index, item) in _list.value.withIndex()) {
            // 未解析ファイル以外はスキップ
            if (item.base.metadata !is StorageItemMetadata.UnanalyzedFile) return
            val metadata = _storageItemListRepository.loadMetadata(item.base.path)
            val itemMetadata =
                if (metadata == null) StorageItemMetadata.InvalidFile else StorageItemMetadata.Audio(
                    metadata
                )
            _storageCache[index] = item.base.copy(metadata = itemMetadata)
        }
        // すべて取得してからupdate
        _list.update { list -> list.toMutableList().apply { _storageCache } }
    }

}



