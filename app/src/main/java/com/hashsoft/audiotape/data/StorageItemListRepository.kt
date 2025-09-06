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
        fun sort(list: List<StorageItemDto>, sortOrder: AudioTapeSortOrder): List<StorageItemDto> {
            return when (sortOrder) {
                AudioTapeSortOrder.NAME_ASC -> list.sortedBy { it.name }
                AudioTapeSortOrder.NAME_DESC -> list.sortedByDescending { it.name }
                else -> list
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
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            StorageHelper.getFileList(path).mapNotNull {
                if (it.isDirectory) {
                    StorageItemDto(it.name, it.path, it.size, it.lastModified)
                } else {
                    val isAudio = StorageHelper.isAudioExtension(it.path)
                    if (isAudio) {
                        StorageItemDto(
                            it.name,
                            it.path,
                            it.size,
                            it.lastModified,
                            StorageItemMetadata.UnanalyzedFile
                        )
                    } else {
                        return@mapNotNull null
                    }
                }
            }
        } else {
            StorageHelper.getFileList(path).map {
                StorageItemDto(
                    it.name,
                    it.path,
                    it.size,
                    it.lastModified,
                    if (it.isDirectory) StorageItemMetadata.Folder else StorageItemMetadata.UnanalyzedFile
                )
            }
        }
//        val checker = AudioFileChecker()
//        val retriever = MediaMetadataRetriever()
//        val result = StorageHelper.getFileList(path).mapNotNull {
//            if (it.isDirectory) {
//                StorageItemDto(it.name, it.path, it.size, it.lastModified)
//            } else {
//                val result = checker.getMetadata(retriever, it.path)
//                result.fold(onSuccess = { metadata ->
//                    StorageItemDto(
//                        it.name, it.path, it.size, it.lastModified, ,
//                        StorageItemMetadata.File
//                    )
//                }, onFailure = {
//                    return@mapNotNull null
//                })
//            }
//        }
//        retriever.release()
//        return result
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