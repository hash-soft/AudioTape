package com.hashsoft.audiotape.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.LibraryStateDto
import com.hashsoft.audiotape.data.LibraryStateRepository
import com.hashsoft.audiotape.data.PlaybackRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.ResumeAudioRepository
import kotlinx.coroutines.launch
import java.io.File

class LibraryStateViewModel(
    private val _controller: AudioController = AudioController(),
    private val _libraryStateRepository: LibraryStateRepository,
    private val _playbackRepository: PlaybackRepository,
    audioTapeRepository: AudioTapeRepository,
    playingStateRepository: PlayingStateRepository,
    resumeAudioRepository: ResumeAudioRepository
) :
    ViewModel() {

    val uiState: MutableState<LibraryStateUiState> = mutableStateOf(LibraryStateUiState.Loading)

    val playItemState = PlayItemState(
        viewModelScope,
        _playbackRepository,
        audioTapeRepository,
        playingStateRepository,
        resumeAudioRepository
    )

    init {
        viewModelScope.launch {
            val state = _libraryStateRepository.getLibraryState()
            uiState.value = LibraryStateUiState.Success(state)
        }
    }

    fun tabs() = _libraryStateRepository.tabs()

    fun saveSelectedTabName(index: Int) = viewModelScope.launch {
        _libraryStateRepository.saveSelectedTabName(index)
    }

    fun play() = _controller.play()

    fun pause() = _controller.pause()

    fun getContentPosition() = _controller.getContentPosition()

    fun seekTo(position: Long) {
        if (_controller.isCurrentMediaItem()) {
            _controller.seekTo(position)
        } else {
            val value = playItemState.item.value
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
}

sealed interface LibraryStateUiState {
    data object Loading : LibraryStateUiState
    data class Success(
        val libraryState: LibraryStateDto
    ) : LibraryStateUiState
}