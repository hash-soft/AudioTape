package com.hashsoft.audiotape.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.ControllerRepository
import com.hashsoft.audiotape.data.DisplayPlayingSource
import com.hashsoft.audiotape.data.LibraryStateDto
import com.hashsoft.audiotape.data.LibraryStateRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.StorageVolumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class LibraryStateViewModel @Inject constructor(
    private val _controller: AudioController,
    private val _libraryStateRepository: LibraryStateRepository,
    controllerRepository: ControllerRepository,
    audioTapeRepository: AudioTapeRepository,
    playingStateRepository: PlayingStateRepository,
    audioStoreRepository: AudioStoreRepository,
    storageVolumeRepository: StorageVolumeRepository,
) :
    ViewModel() {

    val uiState: MutableState<LibraryStateUiState> = mutableStateOf(LibraryStateUiState.Loading)

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

    init {
        viewModelScope.launch {
            val state = _libraryStateRepository.getLibraryState()
            uiState.value = LibraryStateUiState.Success(state)
        }

//        viewModelScope.launch {
//            _playItemState.currentPosition.collect { position ->
//                _currentPosition.update { position }
//            }
//        }
    }

    fun tabs() = _libraryStateRepository.tabs()

    fun saveSelectedTabName(index: Int) = viewModelScope.launch {
        _libraryStateRepository.saveSelectedTabName(index)
    }

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

data class DisplayPlayingItem(
    val audioTape: AudioTapeDto = AudioTapeDto("", ""),
    val audioList: List<AudioItemDto> = listOf(),
    val treeList: List<String>? = null
)


sealed interface LibraryStateUiState {
    data object Loading : LibraryStateUiState
    data class Success(
        val libraryState: LibraryStateDto
    ) : LibraryStateUiState
}