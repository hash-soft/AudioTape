package com.hashsoft.audiotape.data

import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
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

    fun getAudioItem(absolutePath: String): AudioItemDto? {
        return cache.find { it.absolutePath + File.separator + it.name == absolutePath }
    }

    fun getListByPath(path: String): List<AudioItemDto> {
        return cache.filter { it.absolutePath == path }
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

        // Todo Q以降はRELATIVE_PATH、以前はDATAで判別
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DATA, // file path
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

        // show only music
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        val query = context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            null,
        )

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            audioItemListFromCursorQ(query)
        } else {
            audioItemListFromCursorLegacy(query)
        }
    }

    private fun audioItemListFromCursorQ(query: Cursor?): List<AudioItemDto> {
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
            val relativePathColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.RELATIVE_PATH)
            val volumeNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.VOLUME_NAME)
            //val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                val file = File(cursor.getString(dataColumn))
                audioItemList.add(
                    AudioItemDto(
                        name = cursor.getString(nameColumn),
                        absolutePath = file.parent ?: "",
                        relativePath = cursor.getString(relativePathColumn),
                        lastModified = cursor.getLong(dateModifiedColumn) * 1000,
                        id = cursor.getLong(idColumn),
                        size = cursor.getLong(sizeColumn),
                        volumeName = cursor.getString(volumeNameColumn),
                        metadata = AudioItemMetadata(
                            album = cursor.getString(albumColumn),
                            title = cursor.getString(titleColumn),
                            artist = cursor.getString(artistColumn),
                            duration = cursor.getLong(durationColumn)
                        )
                    )
                )

            }
        }
        return audioItemList
    }

    private fun audioItemListFromCursorLegacy(query: Cursor?): List<AudioItemDto> {
        val audioItemList = mutableListOf<AudioItemDto>()
        return audioItemList
    }

    suspend fun getListByPathOrTimeout(path: String, timeoutMillis: Long): List<AudioItemDto>? {
        return withTimeoutOrNull(timeoutMillis) {
            // 初期値が Success だった場合にも即座に返る
            val state = audioLoadState
                .filterIsInstance<AudioLoadState.Success>()
                .first()

            state.audioList.filter { it.absolutePath == path }
        }
    }
}
