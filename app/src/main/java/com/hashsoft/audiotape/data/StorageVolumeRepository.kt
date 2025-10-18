package com.hashsoft.audiotape.data

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import com.hashsoft.audiotape.R
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

class StorageVolumeRepository(
    private val context: Context
) {
    val volumeFlow = MutableStateFlow<List<VolumeItem>>(emptyList())

    fun reload() {
        volumeFlow.value = loadVolumeList()
    }

    fun getVolumeList(): List<VolumeItem> {
        return volumeFlow.value
    }

    private fun loadVolumeList(): List<VolumeItem> {
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val volumes = storageManager.storageVolumes
        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            volumes.mapNotNull {
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
        } else {
            volumes.mapNotNull {
                @SuppressLint("DiscouragedPrivateApi")
                val getPath = StorageVolume::class.java.getDeclaredMethod("getPath")
                val path = getPath.invoke(it) as String?
                if (path != null) {
                    val name = it.getDescription(context)
                        ?: context.getString(R.string.volume_name_unknown)
                    val getLastModifier = StorageVolume::class.java.getDeclaredMethod(
                        "getLastModified"
                    )
                    VolumeItem(name, path, getLastModifier.invoke(it) as Long, "")
                } else {
                    return@mapNotNull null
                }
            }
        }
        return data
    }
}
