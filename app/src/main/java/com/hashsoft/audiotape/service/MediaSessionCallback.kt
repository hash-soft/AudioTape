package com.hashsoft.audiotape.service

import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.MediaItemsWithStartPosition
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import com.hashsoft.audiotape.data.ResumeAudioDto
import com.hashsoft.audiotape.data.ResumeAudioRepository
import com.hashsoft.audiotape.data.StorageItemListUseCase
import com.hashsoft.audiotape.logic.AudioFileChecker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.future.future
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileInputStream


class MediaSessionCallback(
    private val _ioScope: CoroutineScope,
    private val _resumeAudioRepository: ResumeAudioRepository,
    private val _storageItemListUseCase: StorageItemListUseCase
) : MediaSession.Callback {
    private var metadataJob: Job? = null

    @androidx.annotation.OptIn(UnstableApi::class)
    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): MediaSession.ConnectionResult {
        Timber.d("**onConnect session = $session controller = $controller")
        if (session.isMediaNotificationController(controller)) {
            // 通知バーが必要な時だけくるはず
            Timber.d("**isMediaNotificationController")
        }
        return MediaSession.ConnectionResult.AcceptedResultBuilder(session).build()
        //return super.onConnect(session, controller)
    }

    override fun onDisconnected(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ) {
        super.onDisconnected(session, controller)
        Timber.d("**onDisconnected session = $session controller = $controller")
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    override fun onMediaButtonEvent(
        session: MediaSession,
        controllerInfo: MediaSession.ControllerInfo,
        intent: Intent
    ): Boolean {
        Timber.d("##onMediaButtonEvent controllerInfo: $controllerInfo intent: $intent")
        return super.onMediaButtonEvent(session, controllerInfo, intent)
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    override fun onPlaybackResumption(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo
    ): ListenableFuture<MediaItemsWithStartPosition> {
        // Todo playStateとtapeの組み合わせで見たほうがいいかもしれない>永続的なデータからとったほうがいい
        Timber.d("##onPlaybackResumption data: ${_resumeAudioRepository.data.value}")
        val data = _resumeAudioRepository.data.value
        if (data.path.isEmpty()) {
            // 設定する情報がない場合デフォルト
            return super.onPlaybackResumption(mediaSession, controller)
        }
        val settable = SettableFuture.create<MediaItemsWithStartPosition>()
        CoroutineScope(Dispatchers.Unconfined).future {
            // Your app is responsible for storing the playlist, metadata (like title
            // and artwork) of the current item and the start position to use here.
            val playlist = restorePlaylist(data)
            settable.set(
                MediaItemsWithStartPosition(
                    playlist.first,
                    playlist.second,
                    data.contentPosition
                )
            )
            retrieveMetadata(mediaSession, playlist.first)
        }
        return settable
    }

    private fun restorePlaylist(data: ResumeAudioDto): Pair<List<MediaItem>, Int> {
        val file = File(data.path)
        val folderPath = file.parent ?: ""
        val itemList = _storageItemListUseCase.getAudioItemList(folderPath, data.sortOrder)
        val startIndex = itemList.indexOfFirst { it.name == file.name }
        return itemList.map { audio ->
            // MediaItemを作成する
            val metadata = audio.metadata
            MediaItem.Builder().setUri(audio.absolutePath + File.separator + audio.name).setMediaId(audio.id.toString())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(metadata.title.ifEmpty { audio.name })
                        .setArtist(metadata.artist)
                        .setDurationMs(metadata.duration)
                        .setAlbumTitle(metadata.album)
                        .build()
                ).build()
        } to startIndex
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    override fun onSetMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: List<MediaItem>,
        startIndex: Int,
        startPositionMs: Long
    ): ListenableFuture<MediaItemsWithStartPosition> {
        Timber.d("##onSetMediaItems size = ${mediaItems.size}")

        retrieveMetadata(mediaSession, mediaItems)
        val future = SettableFuture.create<MediaItemsWithStartPosition>()
        future.set(MediaItemsWithStartPosition(mediaItems, startIndex, startPositionMs))
        return future
    }

    private suspend fun processMediaItem(
        mediaSession: MediaSession,
        index: Int,
        item: MediaItem
    ) {
        // metadataにタイトルがなくてもファイル名を入れるので設定済みならなにか入っている
        if (item.mediaMetadata.title != null) {
            return
        }
        val uri = item.localConfiguration?.uri
        if (uri == null || (uri.scheme != null && uri.scheme != "file")) {
            return
        }
        val metadata = fetchMetadata(uri) ?: return
        val updatedItem = item.buildUpon()
            .setMediaMetadata(
                metadata
            )
            .build()
        withContext(Dispatchers.Main) {
            mediaSession.player.replaceMediaItem(index, updatedItem)
        }
    }

    private fun fetchMetadata(uri: Uri): MediaMetadata? {
        val retriever = MediaMetadataRetriever()
        return try {
            FileInputStream(uri.path).use { inputStream ->
                retriever.setDataSource(inputStream.fd)
                val result = AudioFileChecker().getMetadata(retriever)
                result.onSuccess { metadata ->
                    return MediaMetadata.Builder()
                        .setTitle(metadata.title.ifEmpty { uri.lastPathSegment })
                        .setArtist(metadata.artist)
                        .setDurationMs(metadata.duration)
                        .setAlbumTitle(metadata.album)
                        .build()
                }
            }
            // 取得できない場合はMediaSession任せ
            null
        } catch (e: Exception) {
            Timber.e(e)
            null
        } finally {
            retriever.release()
        }
    }

    private fun retrieveMetadata(mediaSession: MediaSession, mediaItems: List<MediaItem>) {
        metadataJob?.cancel()
        metadataJob = _ioScope.launch {
            mediaItems.forEachIndexed { index, item ->
                processMediaItem(mediaSession, index, item)
            }
            // Todo 全取得できたら総時間を更新 playback経由で行う
        }
    }
}
