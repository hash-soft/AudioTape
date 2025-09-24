package com.hashsoft.audiotape.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.LibraryStateDto
import com.hashsoft.audiotape.data.LibraryStateRepository
import com.hashsoft.audiotape.data.PlaybackRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.ResumeAudioRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryStateViewModel(
    private val _controller: AudioController = AudioController(),
    private val _libraryStateRepository: LibraryStateRepository,
    private val _playbackRepository: PlaybackRepository,
    audioTapeRepository: AudioTapeRepository,
    playingStateRepository: PlayingStateRepository,
    resumeAudioRepository: ResumeAudioRepository
) :
    ViewModel() {

    val uiState: StateFlow<LibraryStateUiState> =
        _libraryStateRepository.libraryStateFlow().map { state ->
            val value = uiState.value
            if (value is LibraryStateUiState.Success) {
                // タブ位置はpagerが保持しているので2回目以降更新しない
                LibraryStateUiState.Success(value.libraryState.copy(playViewVisible = state.playViewVisible))
            } else {
                LibraryStateUiState.Success(state)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LibraryStateUiState.Loading
        )

    val playItemState = PlayItemState(
        viewModelScope,
        _playbackRepository,
        audioTapeRepository,
        playingStateRepository,
        resumeAudioRepository
    )

    fun saveSelectedPlayViewVisible(visible: Boolean) = viewModelScope.launch {
        _libraryStateRepository.saveSelectedPlayViewVisible(visible)
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
            playItemState.updatePlaybackPosition(position)
        }
    }

    fun seekToNext() = _controller.seekToNext()

    fun seekToPrevious() = _controller.seekToPrevious()

}

sealed interface LibraryStateUiState {
    data object Loading : LibraryStateUiState
    data class Success(
        val libraryState: LibraryStateDto
    ) : LibraryStateUiState
}