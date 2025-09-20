package com.hashsoft.audiotape.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.PlaybackRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File


enum class TapeViewState {
    Start,
    Success,
}

class TapeViewModel(
    private val _controller: AudioController = AudioController(),
    audioTapeRepository: AudioTapeRepository,
    private val _playingStateRepository: PlayingStateRepository,
    private val _playbackRepository: PlaybackRepository,
) :
    ViewModel() {

    private val _state = MutableStateFlow(TapeViewState.Start)
    val state: StateFlow<TapeViewState> = _state

    val tapeListState = TapeListState()

    init {
        viewModelScope.launch {
            // Todo DBのほうにソートを入れるようにする DBにソートに必要な情報が入っているから可能
            combine(
                audioTapeRepository.getAll(),
                _playbackRepository.data,
                _playingStateRepository.playingStateFlow()
            ) { list, playback, playingState ->
                Triple(list, playback, playingState)
            }.collect { (list, playback, playingState) ->
                tapeListState.updateList(list, playback, playingState.folderPath)
                _state.update { TapeViewState.Success }
            }
        }
    }

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

//sealed interface TapeUiState {
//    data object Loading : TapeUiState
//    data class Success(
//        val audioTapeList: List<AudioTapeDto>
//    ) : TapeUiState
//}