package com.hashsoft.audiotape.ui

import android.media.MediaMetadataRetriever
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.PlayAudioDto
import com.hashsoft.audiotape.data.PlaybackDto
import com.hashsoft.audiotape.data.PlaybackRepository
import com.hashsoft.audiotape.data.ResumeAudioRepository
import com.hashsoft.audiotape.logic.AudioFileChecker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.io.File


class PlayItemState(
    private val _playbackRepository: PlaybackRepository,
    private val _audioTapeRepository: AudioTapeRepository,
    private val _resumeAudioRepository: ResumeAudioRepository,
    private val _item: MutableStateFlow<PlayAudioDto?> = MutableStateFlow(null)
) {
    val item: StateFlow<PlayAudioDto?> = _item.asStateFlow()

    fun updatePlayAudioForExclusive(audioTape: AudioTapeDto, playback: PlaybackDto) {
        val path = audioTape.folderPath + File.separator + audioTape.currentName
        // audioTapeが存在しない場合だけnullになる
        val playAudio =
            if (_audioTapeRepository.validAudioTapeDto(audioTape)) {
                val durationMs = if (playback.durationMs < 0) {
                    getDuration(path)
                } else {
                    playback.durationMs
                }
                _resumeAudioRepository.updateAll(
                    path,
                    durationMs,
                    audioTape.position,
                    audioTape.sortOrder
                )
                PlayAudioDto(
                    exist = File(path).isFile,
                    playback.isReadyOk,
                    playback.isPlaying,
                    path,
                    durationMs,
                    audioTape.position,
                    audioTape = audioTape
                )
            } else {
                null
            }
        _item.update { playAudio }
    }

    fun updatePlayAudioForSimple(audioTape: AudioTapeDto, playback: PlaybackDto) {
        val path = audioTape.folderPath + File.separator + audioTape.currentName
        val playAudio =
            if (_audioTapeRepository.validAudioTapeDto(audioTape)) {
                val durationMs = if (playback.durationMs < 0) {
                    // Todo 呼び出される回数が多かったら見直す
                    getDuration(path)
                } else {
                    playback.durationMs
                }
                _resumeAudioRepository.updateAll(
                    path,
                    durationMs,
                    audioTape.position,
                    audioTape.sortOrder
                )
                PlayAudioDto(
                    exist = File(path).isFile,
                    playback.isReadyOk,
                    playback.isPlaying,
                    path,
                    durationMs,
                    audioTape.position,
                    audioTape = audioTape
                )
            } else {
                null
            }
        _item.update { playAudio }
    }

    private fun getDuration(path: String): Long {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(path)
            return AudioFileChecker().getDuration(retriever)
        } catch (e: Exception) {
            Timber.e(e)
        } finally {
            retriever.release()
        }
        return 0
    }

    fun updatePlaybackPosition(position: Long) {
        val value = item.value
        if (value == null) {
            return
        }
        val file = File(value.path)
        _playbackRepository.updateAll(
            value.isReadyOk,
            value.isPlaying,
            file.name,
            file.parent ?: "",
            value.durationMs,
            position
        )
    }

}



