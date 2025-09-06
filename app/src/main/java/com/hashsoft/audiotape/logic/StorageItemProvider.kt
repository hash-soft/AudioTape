package com.hashsoft.audiotape.logic

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.provider.MediaStore
import com.hashsoft.audiotape.R
import timber.log.Timber


class StorageItemProvider {
    companion object {
        fun getVolumeList(context: Context): List<StorageItem> {
            val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val volumes = storageManager.storageVolumes
            val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                volumes.mapNotNull {
                    val directory = it.directory
                    if (directory != null) {
                        val name = it.getDescription(context)
                            ?: context.getString(R.string.volume_name_unknown)
                        StorageItem(
                            name,
                            directory.path,
                            directory.length(),
                            directory.lastModified(),
                            true
                        )
                    } else {
                        return@mapNotNull null
                    }
                }
            } else {
                volumes.mapNotNull {
                    @SuppressLint("DiscouragedPrivateApi")
                    val getPath = StorageVolume::class.java.getDeclaredMethod("getPath")
                    val path = getPath.invoke(it) as String?
                    if (path != null) {
                        val name = it.getDescription(context)
                            ?: context.getString(R.string.volume_name_unknown)
                        StorageItem(name, path, 0, 0, true)
                    } else {
                        return@mapNotNull null
                    }
                }
            }
            return data
        }

        fun getFileList(path: String): List<StorageItem> {
            val file = java.io.File(path)
            return file.listFiles()?.map {
                StorageItem(
                    it.name,
                    it.absolutePath,
                    it.length(),
                    it.lastModified(),
                    it.isDirectory
                )
            } ?: listOf()

        }
    }
}