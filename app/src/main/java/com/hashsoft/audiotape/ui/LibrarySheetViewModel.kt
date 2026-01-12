package com.hashsoft.audiotape.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.ControllerRepository
import com.hashsoft.audiotape.data.DisplayPlayingSource
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.StorageVolumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class LibrarySheetViewModel @Inject constructor(
    private val _controller: AudioController,
    controllerRepository: ControllerRepository,
    audioTapeRepository: AudioTapeRepository,
    playingStateRepository: PlayingStateRepository,
    audioStoreRepository: AudioStoreRepository,
    storageVolumeRepository: StorageVolumeRepository,
) :
    ViewModel() {

    private val _playItemState = PlayItemState(
        controller = _controller,
        audioTapeRepository,
        audioStoreRepository,
        storageVolumeRepository,
        playingStateRepository,
        controllerRepository
    )

    val displayPlayingSource = _playItemState.displayPlayingSource.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DisplayPlayingSource.Pause
    )

    val currentPositionState = _playItemState.currentPosition.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(0),
        -1L
    )

    val displayPlayingState = _playItemState.displayPlayingState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val availableState = _playItemState.availableState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    fun setPlayingParameters(audioTape: AudioTapeDto) {
        _controller.setRepeat(audioTape.repeat)
        _controller.setPlaybackParameters(audioTape.speed, audioTape.pitch)
        _controller.setVolume(audioTape.volume)
    }

    fun play() = _controller.play()

    fun pause() = _controller.pause()

    fun seekTo(position: Long) {
        if (_controller.isCurrentMediaItem()) {
            _controller.seekTo(position)
        } else {
            //_audioTapeStagingRepository.updatePosition(position)
        }
    }

    fun seekToNext() = _controller.seekToNext()

    fun seekToPrevious() = _controller.seekToPrevious()

}
