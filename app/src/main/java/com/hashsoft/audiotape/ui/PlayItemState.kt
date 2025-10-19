package com.hashsoft.audiotape.ui

import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.PlayAudioDto
import com.hashsoft.audiotape.data.PlaybackDto
import com.hashsoft.audiotape.data.PlaybackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File


class PlayItemState(
    private val _playbackRepository: PlaybackRepository,
    private val _audioTapeRepository: AudioTapeRepository,
    private val _audioStoreRepository: AudioStoreRepository,
    private val _item: MutableStateFlow<PlayAudioDto?> = MutableStateFlow(null)
) {
    val item: StateFlow<PlayAudioDto?> = _item.asStateFlow()

    fun updatePlayAudioForExclusive(audioTape: AudioTapeDto, playback: PlaybackDto) {
        val path = audioTape.folderPath + File.separator + audioTape.currentName
        // audioTapeが存在しない場合だけnullになる
        val playAudio =
            if (_audioTapeRepository.validAudioTapeDto(audioTape)) {
                val item = _audioStoreRepository.getAudioItem(path)
                val durationMs = item?.metadata?.duration ?: 0
                PlayAudioDto(
                    exist = item != null,
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

    // ここを改修する
    // 高速で読み出せるのでリストごと取得してしまう
    fun updatePlayAudioForSimple(audioTape: AudioTapeDto, playback: PlaybackDto) {
        val path = audioTape.folderPath + File.separator + audioTape.currentName
        val playAudio =
            if (_audioTapeRepository.validAudioTapeDto(audioTape)) {
                val item = _audioStoreRepository.getAudioItem(path)
                val durationMs = item?.metadata?.duration ?: 0
                PlayAudioDto(
                    exist = item != null,
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



