package com.hashsoft.audiotape.data

import jakarta.inject.Inject


class StorageItemListUseCase @Inject constructor(
    private val _audioStoreRepository: AudioStoreRepository,
) {

    companion object {
        fun sortFoldersAndAudiosSeparately(
            list: MutableList<StorageItem>,
            sortOrder: AudioTapeSortOrder
        ) {
            val folders = list.filterIsInstance<FolderItemDto>()
            val audios = list.filterIsInstance<AudioItemDto>()
            list.clear()
            list.addAll(sortedFolderList(folders, sortOrder) + sortedAudioList(audios, sortOrder))
        }

        fun sortedFolderList(
            list: List<FolderItemDto>,
            sortOrder: AudioTapeSortOrder
        ): List<FolderItemDto> {
            return when (sortOrder) {
                AudioTapeSortOrder.NAME_ASC -> list.sortedBy { it.name }
                AudioTapeSortOrder.NAME_DESC -> list.sortedByDescending { it.name }
                else -> list
            }
        }

        fun sortFolderList(list: MutableList<FolderItemDto>, sortOrder: AudioTapeSortOrder) {
            when (sortOrder) {
                AudioTapeSortOrder.NAME_ASC -> list.sortBy { it.name }
                AudioTapeSortOrder.NAME_DESC -> list.sortByDescending { it.name }
                else -> {}
            }
        }

        fun sortedAudioList(
            list: List<AudioItemDto>,
            sortOrder: AudioTapeSortOrder
        ): List<AudioItemDto> {
            return when (sortOrder) {
                AudioTapeSortOrder.NAME_ASC -> list.sortedBy { it.name }
                AudioTapeSortOrder.NAME_DESC -> list.sortedByDescending { it.name }
                else -> list
            }
        }

        fun sortAudioList(list: MutableList<AudioItemDto>, sortOrder: AudioTapeSortOrder) {
            when (sortOrder) {
                AudioTapeSortOrder.NAME_ASC -> list.sortBy { it.name }
                AudioTapeSortOrder.NAME_DESC -> list.sortByDescending { it.name }
                else -> {}
            }
        }

        fun <T> sortedList(
            list: List<T>,
            sortOrder: AudioTapeSortOrder,
            sortName: (T) -> String
        ): List<T> {
            return when (sortOrder) {
                AudioTapeSortOrder.NAME_ASC -> list.sortedBy { sortName(it) }
                AudioTapeSortOrder.NAME_DESC -> list.sortedByDescending { sortName(it) }
                else -> list
            }
        }
    }

    fun pathToStorageItemList(
        volumes: List<VolumeItem>,
        path: String,
        sortOrder: AudioTapeSortOrder
    ): List<StorageItem> {
        // pathが空の場合ルート
        return if (path.isEmpty()) {
            getRootStorageItemList(volumes, sortOrder)
        } else {
            getStorageItemList(volumes, path, sortOrder)
        }
    }

    private fun getRootStorageItemList(
        volumes: List<VolumeItem>,
        sortOrder: AudioTapeSortOrder
    ): List<FolderItemDto> {
        val list = volumes
            .map { FolderItemDto(it.name, it.path, "", it.lastModified, 0) }
        return sortedFolderList(list, sortOrder)
    }

    private fun getStorageItemList(
        volumes: List<VolumeItem>,
        path: String,
        sortOrder: AudioTapeSortOrder
    ): List<StorageItem> {
        val folderList = getDirectoryList(path)
        val searchItem = AudioStoreRepository.pathToSearchObject(volumes, path)
        val audioList = _audioStoreRepository.getListByPath(searchItem)
        return sortedFolderList(folderList, sortOrder) + sortedAudioList(audioList, sortOrder)
    }

    private fun getDirectoryList(path: String): List<FolderItemDto> {
        val file = java.io.File(path)
        return file.listFiles { file -> file.isDirectory }?.map {
            FolderItemDto(
                it.name,
                it.absolutePath,
                "",
                it.lastModified(),
                0
            )
        } ?: listOf()
    }

    fun getAudioItemList(
        volumes: List<VolumeItem>,
        path: String,
        sortOrder: AudioTapeSortOrder
    ): List<AudioItemDto> {
        val searchItem = AudioStoreRepository.pathToSearchObject(volumes, path)
        val list = _audioStoreRepository.getListByPath(searchItem)
        return sortedAudioList(list, sortOrder)
    }
}