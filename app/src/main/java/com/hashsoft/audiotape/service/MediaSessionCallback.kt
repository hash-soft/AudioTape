package com.hashsoft.audiotape.service

import android.content.ContentUris
import android.content.Intent
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.MediaItemsWithStartPosition
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.StorageItemListUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.future.future
import timber.log.Timber


class MediaSessionCallback(
    private val _audioStoreRepository: AudioStoreRepository,
    private val _playingStateRepository: PlayingStateRepository,
    private val _audioTapeRepository: AudioTapeRepository
) : MediaSession.Callback {

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
        val settable = SettableFuture.create<MediaItemsWithStartPosition>()

        CoroutineScope(Dispatchers.Unconfined).future {
            // Your app is responsible for storing the playlist, metadata (like title
            // and artwork) of the current item and the start position to use here.
            val tape = getPlayingAudioTape()
            if (tape == null) {
                settable.set(MediaItemsWithStartPosition(emptyList(), 0, 0))
                return@future
            }
            val playlist = restorePlaylist(tape)
            // Todo controller設定を行う
            settable.set(
                MediaItemsWithStartPosition(
                    playlist.first,
                    playlist.second,
                    tape.position
                )
            )
        }
        return settable
    }

    private suspend fun getPlayingAudioTape(): AudioTapeDto? {
        val state = _playingStateRepository.playingStateFlow().first()
        val tape = _audioTapeRepository.findByPath(state.folderPath).first()
        return tape
    }

    private suspend fun restorePlaylist(tape: AudioTapeDto): Pair<List<MediaItem>, Int> {
        // Todo キャッシュからではなく、MediaStoreから取得する。デバイス情報も。
        val itemList = _audioStoreRepository.getListByPathOrTimeout(tape.folderPath, 1000)
            ?: return emptyList<MediaItem>() to 0
        val sortedList = StorageItemListUseCase.sortedAudioList(itemList, tape.sortOrder)
        val startIndex = sortedList.indexOfFirst { it.name == tape.currentName }
        return sortedList.map { audio ->
            // MediaItemを作成する
            val metadata = audio.metadata
            val uri =
                ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, audio.id)
            MediaItem.Builder().setUri(uri)
                .setMediaId(audio.id.toString())
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

        val future = SettableFuture.create<MediaItemsWithStartPosition>()
        future.set(MediaItemsWithStartPosition(mediaItems, startIndex, startPositionMs))
        return future
    }
}
