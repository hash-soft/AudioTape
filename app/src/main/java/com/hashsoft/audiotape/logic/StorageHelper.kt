package com.hashsoft.audiotape.logic

import android.content.Context
import android.os.Environment.getExternalStorageDirectory
import android.os.storage.StorageVolume

class StorageHelper {
    companion object {
        fun getHomePath(): String {
            val file = getExternalStorageDirectory()
            return file?.absolutePath ?: ""
        }
    }
}