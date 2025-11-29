package com.hashsoft.audiotape.logic

import android.content.ContentUris
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.hashsoft.audiotape.data.AudioItemDto

class MediaItemHelper {
    companion object {
        /**
         * AudioItemDtoをMediaItemに変換する
         *
         * @param audioItem 変換するAudioItemDto
         * @return 変換されたMediaItem
         */
        fun audioItemToMediaItem(audioItem: AudioItemDto): MediaItem {
            val uri =
                ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    audioItem.id
                )
            val builder = MediaItem.Builder().setUri(uri).setMediaId(audioItem.id.toString())
            val metadata = audioItem.metadata
            val mediaMetadata = MediaMetadata.Builder()
                .setArtist(metadata.artist)
                .setTitle(audioItem.name)
                .setDurationMs(metadata.duration)
                .setAlbumTitle(metadata.album)
                .build()
            return builder.setMediaMetadata(mediaMetadata).build()
        }
    }
}