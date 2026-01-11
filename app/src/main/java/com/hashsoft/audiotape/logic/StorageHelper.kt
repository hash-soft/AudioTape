package com.hashsoft.audiotape.logic

import android.content.Context
import android.os.Build
import android.os.Environment.getExternalStorageDirectory
import android.os.storage.StorageManager
import androidx.annotation.RequiresApi
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.data.ItemStatus
import com.hashsoft.audiotape.data.VolumeItem
import timber.log.Timber
import java.io.File

class StorageHelper {
    companion object {
        fun getHomePath(): String {
            val file = getExternalStorageDirectory()
            return file?.absolutePath ?: ""
        }

        fun findVolumeByPath(list: List<VolumeItem>, path: String): VolumeItem? {
            return list.find { path.startsWith(it.path) }
        }

        fun treeListToString(
            list: List<String>?,
            separator: String = " > ",
            default: String = ""
        ): String {
            return list?.joinToString(separator = separator) ?: default.removePrefix(File.separator)
                .replace(
                    File.separator,
                    separator
                )
        }


        @RequiresApi(Build.VERSION_CODES.R)
        fun getVolumesR(context: Context, storageManager: StorageManager): List<VolumeItem> {
            val volumes = storageManager.storageVolumes
            return volumes.mapNotNull {
                Timber.d("getVolume:${it.getDescription(context)},${it.mediaStoreVolumeName}")
                val directory = it.directory
                val mediaStoreVolumeName = it.mediaStoreVolumeName
                if (directory != null && mediaStoreVolumeName != null) {
                    val name = it.getDescription(context)
                        ?: context.getString(R.string.volume_name_unknown)
                    VolumeItem(
                        name,
                        directory.absolutePath,
                        directory.lastModified(),
                        mediaStorageVolumeName = mediaStoreVolumeName,
                        isRemovable = it.isRemovable
                    )
                } else {
                    return@mapNotNull null
                }
            }
        }

        fun checkState(size: Int, path: String): ItemStatus {
            return if (size > 0) ItemStatus.Normal else {
                if (existFolder(path)) ItemStatus.Disabled else ItemStatus.Missing
            }
        }

        fun existFolder(path: String): Boolean {
            return File(path).exists()
        }

    }

}