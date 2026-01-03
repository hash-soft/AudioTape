package com.hashsoft.audiotape.data

import jakarta.inject.Inject
import java.io.File


class StorageItemListUseCase @Inject constructor(
    private val _audioStoreRepository: AudioStoreRepository,
) {

    companion object {

        fun sortedFolderList(
            list: List<FolderItemDto>,
            sortOrder: AudioTapeSortOrder?
        ): List<FolderItemDto> {
            return when (sortOrder) {
                AudioTapeSortOrder.NAME_ASC -> list.sortedBy { it.name }
                AudioTapeSortOrder.NAME_DESC -> list.sortedByDescending { it.name }
                else -> list
            }
        }

        fun sortedAudioList(
            list: List<AudioItemDto>,
            sortOrder: AudioTapeSortOrder?
        ): List<AudioItemDto> {
            return when (sortOrder) {
                AudioTapeSortOrder.NAME_ASC -> list.sortedBy { it.name }
                AudioTapeSortOrder.NAME_DESC -> list.sortedByDescending { it.name }
                else -> list
            }
        }

    }

    fun pathToStorageItemList(
        volumes: List<VolumeItem>,
        path: String,
        sortOrder: AudioTapeSortOrder?
    ): Pair<List<FolderItemDto>, List<AudioItemDto>> {
        // pathが空の場合ルート
        return if (path.isEmpty()) {
            getRootStorageItemList(volumes, sortOrder)
        } else {
            getStorageItemList(volumes, path, sortOrder)
        }
    }

    private fun getRootStorageItemList(
        volumes: List<VolumeItem>,
        sortOrder: AudioTapeSortOrder?
    ): Pair<List<FolderItemDto>, List<AudioItemDto>> {
        val list = volumes.map {
            val searchItem =
                AudioStoreRepository.pathToSearchObject(volumes, it.path)
            val count = _audioStoreRepository.countByPathRecursively(searchItem)
            FolderItemDto(it.name, it.path, "", it.lastModified, count)
        }
        return sortedFolderList(list, sortOrder) to listOf()
    }

    private fun getStorageItemList(
        volumes: List<VolumeItem>,
        path: String,
        sortOrder: AudioTapeSortOrder?
    ): Pair<List<FolderItemDto>, List<AudioItemDto>> {
        val folderList = getDirectoryList(volumes, path)
        val searchItem = AudioStoreRepository.pathToSearchObject(volumes, path)
        val audioList = _audioStoreRepository.getListByPath(searchItem)
        return sortedFolderList(folderList, sortOrder) to sortedAudioList(audioList, sortOrder)
    }

    private fun getDirectoryList(volumes: List<VolumeItem>, path: String): List<FolderItemDto> {
        val file = File(path)
        return file.listFiles { file -> file.isDirectory }?.map {
            val searchItem =
                AudioStoreRepository.pathToSearchObject(volumes, path + File.separator + it.name)
            val count = _audioStoreRepository.countByPathRecursively(searchItem)
            FolderItemDto(
                it.name,
                it.absolutePath,
                "",
                it.lastModified(),
                count
            )
        } ?: listOf()
    }

}