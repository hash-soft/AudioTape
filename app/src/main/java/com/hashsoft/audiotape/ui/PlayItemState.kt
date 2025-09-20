package com.hashsoft.audiotape.ui

import android.media.MediaMetadataRetriever
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.PlayAudioDto
import com.hashsoft.audiotape.data.PlaybackRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.ResumeAudioRepository
import com.hashsoft.audiotape.logic.AudioFileChecker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File


class PlayItemState(
    externalScope: CoroutineScope,
    private val _playbackRepository: PlaybackRepository,
    private val _audioTapeRepository: AudioTapeRepository,
    playingStateRepository: PlayingStateRepository,
    resumeAudioRepository: ResumeAudioRepository,
    private val _item: MutableStateFlow<PlayAudioDto?> = MutableStateFlow(null)
) {

    val item: StateFlow<PlayAudioDto?> = _item.asStateFlow()

    init {
        externalScope.launch {
            @OptIn(ExperimentalCoroutinesApi::class)
            playingStateRepository.playingStateFlow().flatMapLatest { state ->
                combine(
                    _audioTapeRepository.findByPath(state.folderPath),
                    _playbackRepository.data
                ) { audioTape, playback ->
                    val path = audioTape.folderPath + File.separator + audioTape.currentName
                    if (_audioTapeRepository.validAudioTapeDto(audioTape) && File(path).isFile) {
                        val durationMs = if (playback.durationMs < 0) {
                            // Todo 呼び出される回数が多かったら見直す
                            getDuration(path)
                        } else {
                            playback.durationMs
                        }
                        resumeAudioRepository.updateAll(
                            path,
                            durationMs,
                            audioTape.position,
                            audioTape.sortOrder
                        )
                        PlayAudioDto(
                            playback.isReadyOk,
                            playback.isPlaying,
                            path,
                            durationMs,
                            audioTape.position
                        )
                    } else {
                        null
                    }
                }
            }.collect { playAudio ->
                Timber.d("##audioUpdate: $playAudio")
                _item.update { playAudio }
            }

        }
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



