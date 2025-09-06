package com.hashsoft.audiotape.logic

import android.content.Context
import android.os.Environment.getExternalStorageDirectory

class StorageHelper {
    companion object {
        fun getHomePath(): String {
            val file = getExternalStorageDirectory()
            return file?.absolutePath ?: ""
        }

        fun isAudioExtension(path: String): Boolean = AudioFileChecker.isAudioExtension(path)

        fun getVolumeList(context: Context): List<StorageItem> = StorageItemProvider.getVolumeList(context)
        fun getFileList(path: String): List<StorageItem> = StorageItemProvider.getFileList(path)
    }
}