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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import java.io.File

/**
 * MediaStoreからの音声ファイルの読み込み状態を表す sealed class。
 */
sealed class AudioLoadState {
    /**
     * 読み込み中。
     */
    object Loading : AudioLoadState()

    /**
     * 読み込み成功。
     * @param audioList 読み込んだ音声ファイルのリスト。
     */
    data class Success(val audioList: List<AudioItemDto>) : AudioLoadState()

    /**
     * 読み込みエラー。
     * @param throwable 発生した例外。
     */
    data class Error(val throwable: Throwable) : AudioLoadState()
}

/**
 * MediaStoreから音声ファイルを取得し、キャッシュするリポジトリ。
 *
 * MediaStoreの変更を監視し、変更があった場合はキャッシュを更新する。
 * @param context コンテキスト。
 */
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

        /**
         * ファイルパスを [AudioSearchObject] に変換する。
         *
         * @param volumes ボリューム情報のリスト。
         * @param path ファイルパス。
         * @param name ファイル名。
         * @return [AudioSearchObject]。
         */
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

    private val _updateFlow = MutableSharedFlow<Unit>(replay = 1)
    /**
     * 音声リストの更新を通知するFlow。
     */
    val updateFlow: SharedFlow<Unit> = _updateFlow

    private var cache = listOf<AudioItemDto>()

    private val _audioLoadState = MutableStateFlow<AudioLoadState>(AudioLoadState.Loading)
    /**
     * 音声ファイルの読み込み状態。
     */
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

    /**
     * リソースを解放する。
     */
    fun release() {
        context.contentResolver.unregisterContentObserver(observer)
        scope.cancel()
    }

    /**
     * 指定された検索オブジェクトに一致する音声アイテムを取得する。
     *
     * @param searchItem 検索オブジェクト。
     * @return 見つかった場合は [AudioItemDto]、見つからない場合は null。
     */
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

    /**
     * 指定されたパスにある音声アイテムのリストを取得する。
     *
     * @param searchItem 検索オブジェクト。
     * @return 音声アイテムのリスト。
     */
    fun getListByPath(searchItem: AudioSearchObject): List<AudioItemDto> {
        return when (searchItem) {
            is AudioSearchObject.Direct -> cache.filter { it.absolutePath == searchItem.searchPath }
            is AudioSearchObject.Relative -> cache.filter { it.volumeName == searchItem.volumeName && it.relativePath == searchItem.relativePath }
        }
    }

    /**
     * MediaStoreから音声リストを再読み込みし、キャッシュと状態を更新する。
     */
    private suspend fun changeAudioItemList() {
        _audioLoadState.value = AudioLoadState.Loading
        cache = loadAudioItemList()
        _audioLoadState.value = AudioLoadState.Success(cache)
        _updateFlow.emit(Unit) // 更新通知
    }

    /**
     * MediaStoreから音声アイテムのリストを読み込む。
     *
     * @return 音声アイテムのリスト。
     */
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

    /**
     * Cursorから音声アイテムのリストを生成する (Android R以降)。
     *
     * @param query MediaStoreへのクエリ結果カーソル。
     * @return 音声アイテムのリスト。
     */
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
            }
        }
        return audioItemList
    }

    /**
     * Cursorから音声アイテムのリストを生成する (Android Rより前)。
     * `_data` 列を使用して絶対パスを取得する。
     *
     * @param query MediaStoreへのクエリ結果カーソル。
     * @return 音声アイテムのリスト。
     */
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

    /**
     * 指定されたパスの音声リストをタイムアウト付きで取得する。
     *
     * [audioLoadState] が [AudioLoadState.Success] になるまで待機してからリストを返す。
     *
     * @param path 取得するディレクトリのパス。
     * @param timeoutMillis タイムアウト時間 (ミリ秒)。
     * @return 成功した場合は音声アイテムのリスト、タイムアウトした場合はnull。
     */
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

    /**
     * MediaStoreのURIをファイルパスに変換する。
     *
     * @param uri 変換するURI。
     * @return ファイルパス文字列。変換に失敗した場合は空文字列。
     */
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
