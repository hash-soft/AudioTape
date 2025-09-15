package com.hashsoft.audiotape.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.PlaybackRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File


class TapeViewModel(
    private val _controller: AudioController = AudioController(),
    audioTapeRepository: AudioTapeRepository,
    private val _playingStateRepository: PlayingStateRepository,
    private val _playbackRepository: PlaybackRepository,
) :
    ViewModel() {

    val uiState: StateFlow<TapeUiState> =
        audioTapeRepository.getAll().map {
            TapeUiState.Success(it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TapeUiState.Loading
        )

    fun updatePlayingFolderPath(path: String) = viewModelScope.launch {
        _playingStateRepository.saveFolderPath(path)
    }

    fun setMediaItemsByTape(tape: AudioTapeDto) {
        val path = tape.folderPath + File.separator + tape.currentName
        if (_controller.isCurrentByPath(path)) {
            return
        }
        if (_controller.seekToByPath(path, tape.position)) {
            return
        }
        if (_controller.isCurrentMediaItem()) {
            // 位置を更新する
            _playbackRepository.updateContentPosition(_controller.getContentPosition())
        }
        // folderPathからオーディオファイルを取得する
        val audioPair =
            AudioController.getAudioList(tape.folderPath, tape.currentName, tape.sortOrder)
        _controller.setMediaItems(audioPair.first, audioPair.second, tape.position)
    }

    fun play() = _controller.play()
}

sealed interface TapeUiState {
    data object Loading : TapeUiState
    data class Success(
        val audioTapeList: List<AudioTapeDto>
    ) : TapeUiState
}