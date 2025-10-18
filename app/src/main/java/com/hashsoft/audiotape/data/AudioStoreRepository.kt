package com.hashsoft.audiotape.data

import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import java.io.File

class AudioStoreRepository(
    private val context: Context
) {
    val audioFlow = MutableStateFlow<List<AudioItemDto>>(emptyList())

    suspend fun reload() {
        audioFlow.value = loadAudioItemList()
    }

    fun getListByPath(path: String): List<AudioItemDto> {
        return audioFlow.value.filter { it.absolutePath == path }
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
}
