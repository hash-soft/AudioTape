package com.hashsoft.audiotape.data

import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.storage.StorageManager
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.hashsoft.audiotape.logic.StorageHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import java.io.File

sealed class AudioLoadState {
    object Loading : AudioLoadState()
    data class Success(val audioList: List<AudioItemDto>) : AudioLoadState()
    data class Error(val throwable: Throwable) : AudioLoadState()
}

class AudioStoreRepository(
    private val context: Context
) {
    companion object {
        /**
        Android R以降はRELATIVE_PATH、以前はDATAで判別
        Android QでもRELATIVE_PATH、VOLUME_NAMEは取得できるが
        Android QはStorageVolumeからmediaVolumeNameを取得できないため Android R以降にする
         */
        private val notUseData = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

        fun pathToSearchObject(
            volumes: List<VolumeItem>,
            path: String,
            name: String = ""
        ): AudioSearchObject {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                return AudioSearchObject.Direct(path + if (name.isEmpty()) "" else File.separator + name)
            }
            val volume = StorageHelper.findVolumeByPath(volumes, path)
            return volume?.run {
                val relativePath =
                    if (path.length <= volume.path.length) "" else path.substring(volume.path.length + 1) + File.separator
                AudioSearchObject.Relative(volume.mediaStorageVolumeName, relativePath, name)
            } ?: AudioSearchObject.Relative("", "", name)
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    private val _updateFlow = MutableStateFlow(Unit)
    val updateFlow: StateFlow<Unit> = _updateFlow

    private var cache = listOf<AudioItemDto>()

    private val _audioLoadState = MutableStateFlow<AudioLoadState>(AudioLoadState.Loading)
    val audioLoadState: StateFlow<AudioLoadState> = _audioLoadState

    private val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            scope.launch {
                changeAudioItemList()
            }
        }
    }

    init {
        context.contentResolver.registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            true, // サブディレクトリも監視する
            observer
        )
        // 初回キャッシュロード
        scope.launch { changeAudioItemList() }
    }

    fun release() {
        context.contentResolver.unregisterContentObserver(observer)
        scope.cancel()
    }

    fun getAudioItem(searchItem: AudioSearchObject): AudioItemDto? {
        return when (searchItem) {
            is AudioSearchObject.Direct -> cache.find { it.absolutePath + File.separator + it.name == searchItem.searchPath }
            is AudioSearchObject.Relative -> {
                cache.find {
                    it.volumeName == searchItem.volumeName && it.relativePath == searchItem.relativePath && it.name == searchItem.name
                }
            }
        }
    }

    fun getListByPath(searchItem: AudioSearchObject): List<AudioItemDto> {
        return when (searchItem) {
            is AudioSearchObject.Direct -> cache.filter { it.absolutePath == searchItem.searchPath }
            is AudioSearchObject.Relative -> cache.filter { it.volumeName == searchItem.volumeName && it.relativePath == searchItem.relativePath }
        }
    }

    private suspend fun changeAudioItemList() {
        _audioLoadState.value = AudioLoadState.Loading
        cache = loadAudioItemList()
        _audioLoadState.value = AudioLoadState.Success(cache)
        _updateFlow.emit(Unit) // 更新通知
    }

    private fun loadAudioItemList(): List<AudioItemDto> {
        Timber.d("getAudioItemListFromMediaStore start")

        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

        val projection = if (notUseData) {
            arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATE_MODIFIED,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.RELATIVE_PATH,
                MediaStore.Audio.Media.VOLUME_NAME
            )
        } else {
            arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA, // file path
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATE_MODIFIED,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
            )
        }

        // show only music
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        val query = context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            null,
        )

        return if (notUseData) {
            audioItemListFromCursor(query)
        } else {
            audioItemListFromCursorUseData(query)
        }
    }

    private fun audioItemListFromCursor(query: Cursor?): List<AudioItemDto> {
        val audioItemList = mutableListOf<AudioItemDto>()
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val dateModifiedColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val relativePathColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.RELATIVE_PATH)
            val volumeNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.VOLUME_NAME)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                audioItemList.add(
                    AudioItemDto(
                        name = cursor.getString(nameColumn),
                        absolutePath = "",
                        relativePath = cursor.getString(relativePathColumn),
                        lastModified = cursor.getLong(dateModifiedColumn) * 1000,
                        id = cursor.getLong(idColumn),
                        size = cursor.getLong(sizeColumn),
                        volumeName = cursor.getString(volumeNameColumn),
                        metadata = AudioItemMetadata(
                            album = cursor.getString(albumColumn),
                            title = cursor.getString(titleColumn),
                            artist = cursor.getString(artistColumn),
                            duration = cursor.getLong(durationColumn),
                            albumId = cursor.getLong(albumIdColumn)
                        )
                    )
                )
                Timber.d("getAudioItemListFromMediaStore ${cursor.getString(volumeNameColumn)}")

            }
        }
        return audioItemList
    }

    private fun audioItemListFromCursorUseData(query: Cursor?): List<AudioItemDto> {
        val audioItemList = mutableListOf<AudioItemDto>()
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val dateModifiedColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                val file = File(cursor.getString(dataColumn))
                audioItemList.add(
                    AudioItemDto(
                        name = cursor.getString(nameColumn),
                        absolutePath = file.parent ?: "",
                        relativePath = "",
                        lastModified = cursor.getLong(dateModifiedColumn) * 1000,
                        id = cursor.getLong(idColumn),
                        size = cursor.getLong(sizeColumn),
                        volumeName = "",
                        metadata = AudioItemMetadata(
                            album = cursor.getString(albumColumn),
                            title = cursor.getString(titleColumn),
                            artist = cursor.getString(artistColumn),
                            duration = cursor.getLong(durationColumn),
                            albumId = cursor.getLong(albumIdColumn)
                        )
                    )
                )
            }
        }
        return audioItemList
    }

    suspend fun getListByPathOrTimeout(path: String, timeoutMillis: Long): List<AudioItemDto>? {
        return withTimeoutOrNull(timeoutMillis) {
            // 初期値が Success だった場合にも即座に返る
            val state = audioLoadState
                .filterIsInstance<AudioLoadState.Success>()
                .first()
            val searchItem = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                pathToSearchObject(emptyList(), path)
            } else {
                val volumes = StorageHelper.getVolumesR(
                    context,
                    context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
                )
                pathToSearchObject(volumes, path)
            }
            when (searchItem) {
                is AudioSearchObject.Direct -> state.audioList.filter { it.absolutePath == searchItem.searchPath }
                is AudioSearchObject.Relative -> state.audioList.filter { it.volumeName == searchItem.volumeName && it.relativePath == searchItem.relativePath }
            }
        }
    }

    fun uriToPath(uri: Uri): String {
        val projection = if (notUseData) {
            arrayOf(
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.RELATIVE_PATH,
                MediaStore.Audio.Media.VOLUME_NAME
            )
        } else {
            arrayOf(MediaStore.Audio.Media.DATA)
        }
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (!cursor.moveToFirst()) {
                return@use
            }
            if (notUseData) {
                val name =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                val path =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.RELATIVE_PATH))
                val volume =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.VOLUME_NAME))
                return makeDataPath(volume, path, name)
            } else {
                return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
            }
        }
        return ""
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun makeDataPath(volumeName: String, relativePath: String, fileName: String): String {
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val volumes = StorageHelper.getVolumesR(context, storageManager)
        val volume = volumes.find { it.mediaStorageVolumeName == volumeName } ?: return ""
        return volume.path + File.separator + relativePath + fileName
    }

}
