package com.hashsoft.audiotape.ui

import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.AudioTapeStagingRepository
import com.hashsoft.audiotape.data.ControllerState
import com.hashsoft.audiotape.data.ControllerStateRepository
import com.hashsoft.audiotape.data.PlayAudioDto
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.StorageItemListUseCase
import com.hashsoft.audiotape.data.StorageVolumeRepository
import com.hashsoft.audiotape.data.VolumeItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.io.File


class PlayItemState(
    controller: AudioController,
    private val _audioTapeStagingRepository: AudioTapeStagingRepository,
    private val _audioTapeRepository: AudioTapeRepository,
    private val _audioStoreRepository: AudioStoreRepository,
    storageVolumeRepository: StorageVolumeRepository,
    playingStateRepository: PlayingStateRepository,
    controllerStateRepository: ControllerStateRepository,
    private val _item: MutableStateFlow<PlayAudioDto?> = MutableStateFlow(null)
) {

    private val _baseState = combine(
        storageVolumeRepository.volumeChangeFlow(),
        _audioStoreRepository.updateFlow,
        playingStateRepository.playingStateFlow()
    ) { volumes, _, playingState ->
        volumes to playingState.folderPath
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _refreshAudioState = _baseState.flatMapLatest { pair ->
        val searchObject = AudioStoreRepository.pathToSearchObject(
            pair.first,
            pair.second,
        )
        val list = _audioStoreRepository.getListByPath(searchObject)
        _audioTapeRepository.findSortOrderByPath(pair.second).distinctUntilChanged()
            .map { sortOrder ->
                val sortedList =
                    if (sortOrder == null) list else StorageItemListUseCase.sortedAudioList(
                        list,
                        sortOrder
                    )
                // ソートした時点でcontrollerのプレイリストと合わせる
                controller.replaceMediaItemsWith(sortedList)
                Triple(pair.first, pair.second, sortedList)
            }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private val _refreshItemState = _refreshAudioState.flatMapLatest { triple ->
        _audioTapeRepository.findByPath(triple.second).map { audioTape ->
            if (audioTape == null) null else {
                val treeList =
                    AudioStoreRepository.pathToTreeList(triple.first, audioTape.folderPath)
                Triple(audioTape, triple.third, treeList)
            }
        }
    }

    val displayPlayingState =
        combine(_refreshItemState, controllerStateRepository.data) { audio, controllerState ->
            if (audio == null) null else {
                val audioTape = audio.first
                if (controller.getMediaItemCount() == 0) {
                    controller.setMediaItems(
                        audio.second,
                        audioTape.currentName,
                        audioTape.position
                    )
                }
                DisplayPlayingItem(audioTape, audio.second, audio.third, controllerState)
            }
        }


    val item: StateFlow<PlayAudioDto?> = _item.asStateFlow()

    fun updatePlayAudioForExclusive(
        volumes: List<VolumeItem>,
        audioTape: AudioTapeDto,
        playback: ControllerState
    ) {
        // audioTapeが存在しない場合だけnullになる
        val playAudio =
            if (_audioTapeRepository.validAudioTapeDto(audioTape)) {
                val searchObject = AudioStoreRepository.pathToSearchObject(
                    volumes,
                    audioTape.folderPath,
                    audioTape.currentName
                )
                val item = _audioStoreRepository.getAudioItem(searchObject)
                val durationMs = item?.metadata?.duration ?: 0
                PlayAudioDto(
                    exist = item != null,
                    playback.isReadyOk,
                    playback.isPlaying,
                    audioTape.folderPath + File.separator + audioTape.currentName,
                    durationMs,
                    audioTape.position,
                    audioTape = audioTape
                )
            } else {
                null
            }
        _item.update { playAudio }
    }

    fun updatePlaybackPosition(position: Long) {
        _audioTapeStagingRepository.updatePosition(position)
    }

}



