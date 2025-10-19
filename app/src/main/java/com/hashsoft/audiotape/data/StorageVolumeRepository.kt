package com.hashsoft.audiotape.data

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import com.hashsoft.audiotape.R
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import java.util.concurrent.Executors

class StorageVolumeRepository(
    private val context: Context
) {
    fun volumeChangeFlow(): Flow<List<VolumeItem>> = callbackFlow {
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // API 30+
            val callback = object : StorageManager.StorageVolumeCallback() {
                override fun onStateChanged(volume: StorageVolume) {
                    trySend(loadVolumeList())
                }
            }
            storageManager.registerStorageVolumeCallback(
                Executors.newSingleThreadExecutor(),
                callback
            )
            trySend(loadVolumeList())
            awaitClose { storageManager.unregisterStorageVolumeCallback(callback) }

        } else {
            // 古い端末向け
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_MEDIA_MOUNTED)
                addAction(Intent.ACTION_MEDIA_UNMOUNTED)
                addAction(Intent.ACTION_MEDIA_REMOVED)
                addDataScheme("file")
            }
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(ctx: Context?, intent: Intent?) {
                    trySend(loadVolumeList())
                }
            }
            context.registerReceiver(receiver, filter)
            trySend(loadVolumeList())
            awaitClose { context.unregisterReceiver(receiver) }
        }
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
