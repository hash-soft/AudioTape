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
import com.hashsoft.audiotape.logic.StorageHelper
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
                    trySend(loadVolumeList(storageManager))
                }
            }
            storageManager.registerStorageVolumeCallback(
                Executors.newSingleThreadExecutor(),
                callback
            )
            trySend(loadVolumeList(storageManager))
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
                    trySend(loadVolumeList(storageManager))
                }
            }
            context.registerReceiver(receiver, filter)
            trySend(loadVolumeList(storageManager))
            awaitClose { context.unregisterReceiver(receiver) }
        }
    }

    private fun loadVolumeList(storageManager: StorageManager): List<VolumeItem> {
        val volumes = storageManager.storageVolumes
        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            StorageHelper.getVolumesR(context, storageManager)
        } else {
            volumes.mapNotNull {
                @SuppressLint("DiscouragedPrivateApi")
                val getPath = StorageVolume::class.java.getDeclaredMethod("getPath")
                val path = getPath.invoke(it) as String?
                if (path != null) {
                    val name = it.getDescription(context)
                        ?: context.getString(R.string.volume_name_unknown)
                    VolumeItem(name, path, 0, "", it.isRemovable)
                } else {
                    return@mapNotNull null
                }
            }
        }
        return data
    }
}
