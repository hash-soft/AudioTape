package com.hashsoft.audiotape.data

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Build
import com.hashsoft.audiotape.logic.AudioFileChecker
import com.hashsoft.audiotape.logic.StorageHelper
import timber.log.Timber
import java.io.FileInputStream

// ソートをどうするか
class StorageItemListRepository(private val _context: Context) {
    companion object {
        fun sorted(
            list: List<StorageItemDto>,
            sortOrder: AudioTapeSortOrder
        ): List<StorageItemDto> {
            return when (sortOrder) {
                AudioTapeSortOrder.NAME_ASC -> list.sortedBy { it.name }
                AudioTapeSortOrder.NAME_DESC -> list.sortedByDescending { it.name }
                else -> list
            }
        }

        fun sort(list: MutableList<StorageItemDto>, sortOrder: AudioTapeSortOrder) {
            when (sortOrder) {
                AudioTapeSortOrder.NAME_ASC -> list.sortBy { it.name }
                AudioTapeSortOrder.NAME_DESC -> list.sortByDescending { it.name }
                else -> {}
            }
        }
    }

    fun pathToStorageItemList(path: String): List<StorageItemDto> {
        // pathが空の場合ルート
        return if (path.isEmpty()) {
            getRootStorageItemList()
        } else {
            getStorageItemList(path)
        }
    }

    private fun getRootStorageItemList(): List<StorageItemDto> {
        return StorageHelper.getVolumeList(_context)
            .map { StorageItemDto(it.name, it.path, it.size, it.lastModified) }
    }

    private fun getStorageItemList(path: String): List<StorageItemDto> {
        val directoryList = StorageHelper.getDirectoryList(path).map{
            StorageItemDto(it.name, it.path, it.size, it.lastModified)
        }
        val audioList = AudioItemListRepository(_context).getAudioItemListFromMediaStore(path,
            AudioTapeSortOrder.NAME_ASC)
        return directoryList + audioList
    }

    fun loadMetadata(path: String): AudioItemMetadata? {
        val retriever = MediaMetadataRetriever()
        try {
            FileInputStream(path).use { inputStream ->
                val checker = AudioFileChecker()
                retriever.setDataSource(inputStream.fd)
                if (checker.isAudio(retriever)) {
                    val result = checker.getMetadata(retriever)
                    if (result.isSuccess) {
                        return result.getOrNull()
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        } finally {
            retriever.release()
        }
        return null
    }
}