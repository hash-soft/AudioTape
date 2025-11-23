package com.hashsoft.audiotape.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.AudioTapeStagingRepository
import com.hashsoft.audiotape.data.ContentPositionRepository
import com.hashsoft.audiotape.data.ControllerState
import com.hashsoft.audiotape.data.ControllerStateRepository
import com.hashsoft.audiotape.data.LibraryStateDto
import com.hashsoft.audiotape.data.LibraryStateRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.StorageVolumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class LibraryStateViewModel @Inject constructor(
    private val _controller: AudioController,
    private val _libraryStateRepository: LibraryStateRepository,
    controllerStateRepository: ControllerStateRepository,
    private val _audioTapeStagingRepository: AudioTapeStagingRepository,
    private val _audioTapeRepository: AudioTapeRepository,
    playingStateRepository: PlayingStateRepository,
    private val _audioStoreRepository: AudioStoreRepository,
    storageVolumeRepository: StorageVolumeRepository,
    contentPositionRepository: ContentPositionRepository
) :
    ViewModel() {

    val uiState: MutableState<LibraryStateUiState> = mutableStateOf(LibraryStateUiState.Loading)

    private val _basePlayingState = combine(
        storageVolumeRepository.volumeChangeFlow(),
        _audioStoreRepository.updateFlow,
        playingStateRepository.playingStateFlow()
    ) { volumes, _, playingState ->
        volumes to playingState.folderPath
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _itemPlayingState = _basePlayingState.flatMapLatest { pair ->
        _audioTapeRepository.findByPath(pair.second).map { audioTape ->
            if (audioTape == null) null else {
                val treeList = AudioStoreRepository.pathToTreeList(pair.first, audioTape.folderPath)
                val searchObject = AudioStoreRepository.pathToSearchObject(
                    pair.first,
                    audioTape.folderPath,
                    audioTape.currentName
                )
                Triple(audioTape, _audioStoreRepository.getAudioItem(searchObject), treeList)
            }
        }
    }

    val displayPlayingState =
        combine(_itemPlayingState, controllerStateRepository.data) { audio, controllerState ->
            if (audio == null) null else {
                DisplayPlayingItem(audio.first, audio.second, audio.third, controllerState)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val playingPosition = contentPositionRepository.value.asStateFlow()

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

    fun setPlayingParameters(audioTape: AudioTapeDto) {
        _controller.setRepeat(audioTape.repeat)
        _controller.setPlaybackParameters(audioTape.speed, audioTape.pitch)
        _controller.setVolume(audioTape.volume)
    }

    fun play() = _controller.play()

    fun pause() = _controller.pause()

    fun getContentPosition() = _controller.getContentPosition()

    fun seekTo(position: Long) {
        if (_controller.isCurrentMediaItem()) {
            _controller.seekTo(position)
        } else {
            _audioTapeStagingRepository.updatePosition(position)
        }
    }

    fun seekToNext() = _controller.seekToNext()

    fun seekToPrevious() = _controller.seekToPrevious()

}

data class DisplayPlayingItem(
    val audioTape: AudioTapeDto = AudioTapeDto("", ""),
    val audioItem: AudioItemDto?,
    val treeList: List<String>?,
    val controllerState: ControllerState = ControllerState(isReadyOk = false, isPlaying = false)
)


sealed interface LibraryStateUiState {
    data object Loading : LibraryStateUiState
    data class Success(
        val libraryState: LibraryStateDto
    ) : LibraryStateUiState
}