package com.hashsoft.audiotape.ui

import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.AudioTapeStagingRepository
import com.hashsoft.audiotape.data.ControllerState
import com.hashsoft.audiotape.data.PlayAudioDto
import com.hashsoft.audiotape.data.VolumeItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File


class PlayItemState(
    private val _audioTapeStagingRepository: AudioTapeStagingRepository,
    private val _audioTapeRepository: AudioTapeRepository,
    private val _audioStoreRepository: AudioStoreRepository,
    private val _item: MutableStateFlow<PlayAudioDto?> = MutableStateFlow(null)
) {
    val item: StateFlow<PlayAudioDto?> = _item.asStateFlow()

    fun updatePlayAudioForExclusive(
        volumes: List<VolumeItem>,
        audioTape: AudioTapeDto,
        playback: ControllerState
    ) {
        // audioTapeが存在しない場合だけnullになる
        val playAudio =
            if (_audioTapeRepository.validAudioTapeDto(audioTape)) {
                val searchObject = AudioStoreRepository.pathToSearchObject(
                    volumes,
                    audioTape.folderPath,
                    audioTape.currentName
                )
                val item = _audioStoreRepository.getAudioItem(searchObject)
                val durationMs = item?.metadata?.duration ?: 0
                PlayAudioDto(
                    exist = item != null,
                    playback.isReadyOk,
                    playback.isPlaying,
                    audioTape.folderPath + File.separator + audioTape.currentName,
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
        _audioTapeStagingRepository.updatePosition(position)
    }

}



