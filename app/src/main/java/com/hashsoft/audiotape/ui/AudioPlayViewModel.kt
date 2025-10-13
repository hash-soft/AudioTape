package com.hashsoft.audiotape.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.PlaybackRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.ResumeAudioRepository
import com.hashsoft.audiotape.data.StorageItemListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@HiltViewModel
class AudioPlayViewModel @Inject constructor(
    private val _controller: AudioController,
    playbackRepository: PlaybackRepository,
    audioTapeRepository: AudioTapeRepository,
    playingStateRepository: PlayingStateRepository,
    resumeAudioRepository: ResumeAudioRepository,
    storageItemListRepository: StorageItemListRepository,
) :
    ViewModel() {

    val playItemState = PlayItemState(
        playbackRepository,
        audioTapeRepository,
        resumeAudioRepository
    )

    val playListState = PlayListState(
        storageItemListRepository
    )


    init {
        viewModelScope.launch {
            @OptIn(ExperimentalCoroutinesApi::class)
            playingStateRepository.playingStateFlow().flatMapLatest { state ->
                playListState.loadStorageCache(state.folderPath)
                combine(
                    audioTapeRepository.findByPath(state.folderPath),
                    playbackRepository.data
                ) { audioTape, playback ->
                    audioTape to playback
                }
            }.collect { (audioTape, playback) ->
                playListState.updateList(audioTape)
                playItemState.updatePlayAudio(audioTape, playback)
                playListState.loadMetadata()
            }
        }
    }

    fun play() = _controller.play()

    fun pause() = _controller.pause()

}
