package com.hashsoft.audiotape.data

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * オーディオアイテムのリストを取得するためのリポジトリクラス。
 * MediaStoreまたはファイルシステムからオーディオファイルを取得する
 *
 * @param context ContentResolverにアクセスするために使用されるアプリケーションコンテキスト
 */
class AudioItemListRepository(
    private val context: Context
) {
    /**
     * MediaStoreからオーディオファイルのリストを取得する
     *
     * @param path 検索対象のファイルパス。このパスで始まるオーディオファイルが検索されます
     * @param sortOrder 結果をソートするための順序
     * @return 指定されたパスとソート順に一致する[StorageItemDto]のリスト
     */
    fun getAudioItemListFromMediaStore(
        path: String,
        sortOrder: AudioTapeSortOrder
    ): List<StorageItemDto> {
        Timber.d("getAudioItemListFromMediaStore start")
        val audioItemList = mutableListOf<StorageItemDto>()
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
        val selection =
            MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Media.DATA + " LIKE ?"
        val selectionArgs = arrayOf("$path%")
        //val selection: String = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + "${MediaStore.Audio.Media.RELATIVE_PATH} = ?"
        //val selectionArgs = arrayOf(path + "")

        val sortColumn = when (sortOrder) {
            AudioTapeSortOrder.NAME_ASC -> "${MediaStore.Audio.Media.DATA} ASC"
            AudioTapeSortOrder.NAME_DESC -> "${MediaStore.Audio.Media.DATA} DESC"
            AudioTapeSortOrder.DATE_ASC -> "${MediaStore.Audio.Media.DATE_MODIFIED} ASC"
            AudioTapeSortOrder.DATE_DESC -> "${MediaStore.Audio.Media.DATE_MODIFIED} DESC"
        }

        val query = context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortColumn
        )

        query?.use { cursor ->
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
                val filePath = cursor.getString(dataColumn)
                val file = File(filePath)
                if (file.exists() && file.parent == path) {
                    //val albumId = cursor.getLong(albumIdColumn)
                    //val artwork = loadArtwork(albumId)
                    val metadata = AudioItemMetadata(
                        album = cursor.getString(albumColumn),
                        title = cursor.getString(titleColumn),
                        artist = cursor.getString(artistColumn),
                        duration = cursor.getLong(durationColumn),
                        //artwork = artwork
                    )
                    audioItemList.add(
                        StorageItemDto(
                            name = cursor.getString(nameColumn),
                            path = filePath,
                            size = cursor.getLong(sizeColumn),
                            lastModified = cursor.getLong(dateModifiedColumn) * 1000,
                            metadata = StorageItemMetadata.Audio(metadata)
                        )
                    )
                }
            }
        }
        Timber.d("getAudioItemListFromMediaStore end")
        return audioItemList
    }

    /**
     * 指定されたアルバムIDに対応するアートワークをロードする
     *
     * @param albumId アートワークを取得するアルバムのID
     * @return アートワークのバイト配列のリスト。アートワークが見つからない場合やエラーが発生した場合は空のリスト
     */
    private fun loadArtwork(albumId: Long): List<Byte> {
        try {
            val artworkUri = "content://media/external/audio/albumart".toUri()
            val path = ContentUris.withAppendedId(artworkUri, albumId)
            val source =
                ImageDecoder.createSource(context.contentResolver, path)
            val bitmap = ImageDecoder.decodeBitmap(source)
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            return stream.toByteArray().toList()
        } catch (e: Exception) {
            return emptyList()
        }
    }
}
