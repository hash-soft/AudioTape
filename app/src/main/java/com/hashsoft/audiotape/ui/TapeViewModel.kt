package com.hashsoft.audiotape.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.ControllerStateRepository
import com.hashsoft.audiotape.data.FolderStateRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.StorageItemListUseCase
import com.hashsoft.audiotape.data.StorageVolumeRepository
import com.hashsoft.audiotape.data.VolumeItem
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File


enum class TapeViewState {
    Start,
    Success,
}

@HiltViewModel
class TapeViewModel @Inject constructor(
    private val _controller: AudioController,
    audioTapeRepository: AudioTapeRepository,
    private val _playingStateRepository: PlayingStateRepository,
    private val _controllerStateRepository: ControllerStateRepository,
    private val _audioTapeRepository: AudioTapeRepository,
    private val _folderStateRepository: FolderStateRepository,
    private val _storageItemListUseCase: StorageItemListUseCase,
    private val _audioStoreRepository: AudioStoreRepository,
    private val _storageVolumeRepository: StorageVolumeRepository
) :
    ViewModel() {

    private val _state = MutableStateFlow(TapeViewState.Start)
    val state: StateFlow<TapeViewState> = _state

    val tapeListState = TapeListState()

    private lateinit var _volumes: List<VolumeItem>

    init {
        viewModelScope.launch {
            @OptIn(ExperimentalCoroutinesApi::class)
            _storageVolumeRepository.volumeChangeFlow().flatMapLatest { volumes ->
                _volumes = volumes
                _audioStoreRepository.updateFlow.flatMapLatest {
                    // Todo DBのほうにソートを入れるようにする DBにソートに必要な情報が入っているから可能
                    combine(
                        audioTapeRepository.getAll(),
                        _controllerStateRepository.data,
                        _playingStateRepository.playingStateFlow()
                    ) { list, playback, playingState ->
                        Triple(list, playback, playingState)
                    }
                }
            }.collect { (list, controllerState, playingState) ->
                tapeListState.updateList(list, controllerState, playingState.folderPath)
                _state.update { TapeViewState.Success }
            }
        }
    }

    fun updatePlayingFolderPath(path: String) = viewModelScope.launch {
        _playingStateRepository.saveFolderPath(path)
    }

    fun setCurrentMediaItemsPosition(tape: AudioTapeDto): Boolean {
        // folderPathからオーディオファイルを取得する
        val audioList =
            _storageItemListUseCase.getAudioItemList(_volumes, tape.folderPath, tape.sortOrder)
        val startIndex = audioList.indexOfFirst { it.name == tape.currentName }
        val id = audioList.getOrNull(startIndex)?.id ?: 0
        if (_controller.isCurrentById(id)) {
            return true
        }
        if (_controller.seekToById(id, tape.position)) {
            return true
        }
        return false
    }

    fun switchPlayingFolder(audioTape: AudioTapeDto) {
        // MediaItemの変更前に通知がこないので前のcurrentの位置を更新する
        updatePrevPlayingPosition()
        _controller.clearMediaItems()
        updatePlaying(audioTape)
    }

    private fun updatePrevPlayingPosition() {
        val position = _controller.getContentPosition()
        _controller.getCurrentMediaItemUri()?.let { uri ->
            viewModelScope.launch {
                val file = File(_audioStoreRepository.uriToPath(uri))
                _audioTapeRepository.updatePlayingPosition(
                    file.parent ?: "",
                    file.name,
                    position,
                    false
                )
            }
        }
    }

    private fun updatePlaying(audioTape: AudioTapeDto) = viewModelScope.launch {
        _playingStateRepository.saveFolderPath(audioTape.folderPath)
    }

    fun playWhenReady(playWhenReady: Boolean) = _controller.playWhenReady(playWhenReady)

    fun setPlayingParameters(audioTape: AudioTapeDto) {
        _controller.setRepeat(audioTape.repeat)
        _controller.setVolume(audioTape.volume)
        _controller.setPlaybackParameters(audioTape.speed, audioTape.pitch)
    }

    fun play() = _controller.play()

    fun saveSelectedPath(path: String) = viewModelScope.launch {
        _folderStateRepository.saveSelectedPath(path)
    }
}
