package com.hashsoft.audiotape.logic

import android.content.Context
import android.os.Build
import android.os.Environment.getExternalStorageDirectory
import android.os.storage.StorageManager
import androidx.annotation.RequiresApi
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.data.VolumeItem
import timber.log.Timber

class StorageHelper {
    companion object {
        fun getHomePath(): String {
            val file = getExternalStorageDirectory()
            return file?.absolutePath ?: ""
        }

        fun pathToVolumeName(list: List<VolumeItem>, path: String): String? {
            list.find { path.startsWith(it.path) }?.let {
                return it.mediaStorageVolumeName
            }
            return null
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
                        mediaStoreVolumeName
                    )
                } else {
                    return@mapNotNull null
                }
            }
        }
    }
}