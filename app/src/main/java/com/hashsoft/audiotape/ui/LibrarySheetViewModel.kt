package com.hashsoft.audiotape.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.LibraryStateDto
import com.hashsoft.audiotape.data.LibraryStateRepository
import com.hashsoft.audiotape.data.PlaybackDto
import com.hashsoft.audiotape.data.PlaybackRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.StorageVolumeRepository
import com.hashsoft.audiotape.data.VolumeItem
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@HiltViewModel
class LibraryStateViewModel @Inject constructor(
    private val _controller: AudioController,
    private val _libraryStateRepository: LibraryStateRepository,
    private val _playbackRepository: PlaybackRepository,
    private val _audioTapeRepository: AudioTapeRepository,
    private val _playingStateRepository: PlayingStateRepository,
    private val _audioStoreRepository: AudioStoreRepository,
    private val _storageVolumeRepository: StorageVolumeRepository
) :
    ViewModel() {

    val controllerOk = _controller.isReady
    val uiState: MutableState<LibraryStateUiState> = mutableStateOf(LibraryStateUiState.Loading)

    val playItemState = PlayItemState(
        _playbackRepository,
        _audioTapeRepository,
        _audioStoreRepository
    )

    init {
        viewModelScope.launch {
            val state = _libraryStateRepository.getLibraryState()
            uiState.value = LibraryStateUiState.Success(state)
            viewModelScope.launch {
                @OptIn(ExperimentalCoroutinesApi::class)
                _storageVolumeRepository.volumeChangeFlow().flatMapLatest { volumes ->
                    watchAudioStore(volumes)
                }.collect { (volumes, audioTape, playback) ->
                    playItemState.updatePlayAudioForSimple(volumes, audioTape, playback)
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun watchAudioStore(volumes: List<VolumeItem>): Flow<Triple<List<VolumeItem>, AudioTapeDto, PlaybackDto>> {
        // 待つだけ
        return _audioStoreRepository.updateFlow.flatMapLatest { watchPlayingState(volumes) }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun watchPlayingState(volumes: List<VolumeItem>): Flow<Triple<List<VolumeItem>, AudioTapeDto, PlaybackDto>> {
        return _playingStateRepository.playingStateFlow().flatMapLatest { state ->
            combine(
                _audioTapeRepository.findByPath(state.folderPath),
                _playbackRepository.data
            ) { audioTape, playback ->
                Triple(volumes, audioTape, playback)
            }
        }
    }

    fun tabs() = _libraryStateRepository.tabs()

    fun saveSelectedTabName(index: Int) = viewModelScope.launch {
        _libraryStateRepository.saveSelectedTabName(index)
    }

    fun setPlayingParameters() {
        playItemState.item.value?.audioTape?.let {
            _controller.setRepeat(it.repeat)
            _controller.setVolume(it.volume)
            _controller.setPlaybackParameters(it.speed, it.pitch)
        }
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