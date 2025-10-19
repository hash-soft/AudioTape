package com.hashsoft.audiotape.logic

import android.os.Environment.getExternalStorageDirectory
import com.hashsoft.audiotape.data.VolumeItem

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
    }
}