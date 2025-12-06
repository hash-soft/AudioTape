package com.hashsoft.audiotape.ui

import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.ControllerStateRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.StorageItemListUseCase
import com.hashsoft.audiotape.data.StorageVolumeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import timber.log.Timber


class PlayItemState(
    controller: AudioController,
    private val _audioTapeRepository: AudioTapeRepository,
    private val _audioStoreRepository: AudioStoreRepository,
    storageVolumeRepository: StorageVolumeRepository,
    playingStateRepository: PlayingStateRepository,
    controllerStateRepository: ControllerStateRepository,
) {

    private val _baseState = combine(
        storageVolumeRepository.volumeChangeFlow(),
        _audioStoreRepository.updateFlow,
        playingStateRepository.playingStateFlow()
    ) { volumes, _, playingState ->
        Timber.d("#5 playingState = $playingState")
        val audioTape = _audioTapeRepository.getByPath(playingState.folderPath)
        if (audioTape != null) {
            val treeList =
                AudioStoreRepository.pathToTreeList(volumes, audioTape.folderPath)
            val searchObject = AudioStoreRepository.pathToSearchObject(
                volumes,
                audioTape.folderPath,
            )
            val list = _audioStoreRepository.getListByPath(searchObject)
            val sortedList = StorageItemListUseCase.sortedAudioList(list, audioTape.sortOrder)
            if (controller.getMediaItemCount() == 0) {
                controller.setMediaItems(sortedList, audioTape.currentName, audioTape.position)
                controller.prepare()
            } else {
                controller.replaceMediaItemsWith(sortedList)
            }
            Triple(audioTape, sortedList, treeList)
        } else null
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _refreshAudioState = _baseState.flatMapLatest { triple ->
        Timber.d("#5 audioState = $triple")
        if (triple != null) {
            var prevSortOrder = triple.first.sortOrder
            _audioTapeRepository.findByPath(triple.first.folderPath).map { audioTape ->
                Timber.d("#5 tape changed = $audioTape")
                if (audioTape == null) null else {
                    // ソートが変更されていた場合更新を行う
                    val sortedList = if (audioTape.sortOrder != prevSortOrder) {
                        val result = StorageItemListUseCase.sortedAudioList(
                            triple.second,
                            audioTape.sortOrder
                        )
                        controller.sortMediaItems(result)
                        prevSortOrder = audioTape.sortOrder
                        result
                    } else {
                        triple.second
                    }
                    Triple(audioTape, sortedList, triple.third)
                }
            }
        } else flowOf(null)

    }

    val displayPlayingState =
        combine(_refreshAudioState, controllerStateRepository.data) { audio, controllerState ->
            Timber.d("#5 displayPlayingState = $audio")
            if (audio == null) null else {
                DisplayPlayingItem(audio.first, audio.second, audio.third, controllerState)
            }
        }

}



