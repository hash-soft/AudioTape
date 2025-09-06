package com.hashsoft.audiotape.ui

import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.StorageItemDto
import com.hashsoft.audiotape.data.StorageItemListRepository
import com.hashsoft.audiotape.data.StorageItemMetadata
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FolderListState(
    private val _storageItemListRepository: StorageItemListRepository,
    private val _list: MutableStateFlow<List<StorageItemDto>> = MutableStateFlow(
        emptyList()
    ),
    private val _typeIndexList: MutableStateFlow<List<Int>> = MutableStateFlow(emptyList())
) {
    val list: StateFlow<List<StorageItemDto>> = _list.asStateFlow()
    val typeIndexList: StateFlow<List<Int>> = _typeIndexList.asStateFlow()

    fun loadList(path: String, audioTape: AudioTapeDto) {
        val storageItemList = _storageItemListRepository.pathToStorageItemList(path)
        val sortList = StorageItemListRepository.sort(storageItemList, audioTape.sortOrder)
        val typeIndexList = makeTypeIndexList(sortList)
        _list.update { sortList }
        _typeIndexList.update { typeIndexList }
    }

    private fun makeTypeIndexList(list: List<StorageItemDto>): List<Int> {
        var folderCount = 0
        var fileCount = 0
        return list.map {
            when (it.metadata) {
                is StorageItemMetadata.Folder -> folderCount++
                else -> fileCount++
            }
        }
    }

    fun loadMetadataByIndex(index: Int) {
        val item = _list.value.getOrNull(index)
        // 未解析ファイル以外はスキップ
        if (item == null || item.metadata !is StorageItemMetadata.UnanalyzedFile) return
        val metadata = _storageItemListRepository.loadMetadata(item.path)
        val itemMetadata =
            if (metadata == null) StorageItemMetadata.InvalidFile else StorageItemMetadata.Audio(
                metadata
            )
        _list.update { list ->
            list.toMutableList().apply {
                this[index] = item.copy(metadata = itemMetadata)
            }
        }
    }

}



