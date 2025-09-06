package com.hashsoft.audiotape.logic

import android.media.MediaMetadataRetriever
import com.hashsoft.audiotape.data.AudioItemMetadata
import timber.log.Timber

class AudioFileChecker() {

    companion object {
        private val audioExtensions = setOf(
            "mp3", "wav", "wave", "aac", "m4a", "flac", "ogg", "oga", "opus",
            "wma", "asf", "aiff", "aif", "amr", "ape", "wv",
            "mid", "midi", "rmi", "mkv", "xmf", "mxmf", "ota"
        )

        fun isAudioExtension(path: String): Boolean {
            val ext = path.substringAfterLast('.', "").lowercase()
            Timber.d("ext: $ext")
            return audioExtensions.contains(ext)
        }
    }


    fun isAudio(retriever: MediaMetadataRetriever): Boolean {
        return try {
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO) != null
        } catch (_: Exception) {
            // 例外の発生で判断する処理のためログには出さない
            false
        }
    }

    fun getMetadata(
        retriever: MediaMetadataRetriever
    ): Result<AudioItemMetadata> {
        val artwork = retriever.embeddedPicture?.toList() ?: emptyList()
        return Result.success(
            AudioItemMetadata(
                album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                    ?: "",
                title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                    ?: "",
                artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                    ?: "",
                duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    ?.toLong() ?: 0L,
                artwork = artwork
            )
        )

    }

}