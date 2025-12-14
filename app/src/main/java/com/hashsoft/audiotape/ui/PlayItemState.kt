package com.hashsoft.audiotape.ui

import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.ControllerRepository
import com.hashsoft.audiotape.data.PlaybackPositionSource
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.StorageItemListUseCase
import com.hashsoft.audiotape.data.StorageVolumeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.isActive
import timber.log.Timber


class PlayItemState(
    controller: AudioController,
    private val _audioTapeRepository: AudioTapeRepository,
    private val _audioStoreRepository: AudioStoreRepository,
    storageVolumeRepository: StorageVolumeRepository,
    playingStateRepository: PlayingStateRepository,
    controllerRepository: ControllerRepository
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
            Triple(audioTape, sortedList, treeList)
        } else null
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val displayPlayingState = _baseState.flatMapLatest { triple ->
        Timber.d("#5 audioState")
        if (triple != null) {
            var prevSortOrder = triple.first.sortOrder
            _audioTapeRepository.findByPath(triple.first.folderPath).map { audioTape ->
                Timber.d("#5 tape changed = $audioTape prevSortOrder = $prevSortOrder")
                if (audioTape == null) null else {
                    val sortedList = if (audioTape.sortOrder != prevSortOrder) {
                        // ソートが変更されていた場合リストとplayerの更新を行う
                        val result = StorageItemListUseCase.sortedAudioList(
                            triple.second,
                            audioTape.sortOrder
                        )
                        controller.sortMediaItems(result)
                        result
                    } else {
                        // ソートが変更されていなかった場合playerだけ更新を行う
                        // リストは_baseState.secondで実行済みだがplayerのほうはif(true)のほうで更新されている
                        // 可能性があるので_baseState.secondに合わせる必要がある
                        controller.sortMediaItems(triple.second)
                        triple.second
                    }
                    DisplayPlayingItem(audioTape, sortedList, triple.third)
                }
            }
        } else flowOf(null)

    }

    val availableState =
        combine(_baseState, controller.availableStateFlow) { audio, available ->
            Timber.d("#5 availableState = $available")
            if (audio != null) {
                if (controller.getMediaItemCount() == 0) {
                    val audioTape = audio.first
                    controller.setMediaItems(
                        audio.second,
                        audioTape.currentName,
                        audioTape.position
                    )
                    controller.prepare()
                    controller.setSpeed(audioTape.speed)
                    controller.setPitch(audioTape.pitch)
                    controller.setRepeat(audioTape.repeat)
                } else {
                    controller.replaceMediaItemsWith(audio.second)
                }
            }
            available
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentPosition = controllerRepository.playbackPositionSource.transformLatest { source ->
        when (source) {
            PlaybackPositionSource.None -> {
                emit(-1L)
            }

            PlaybackPositionSource.Player -> {
                while (currentCoroutineContext().isActive) {
                    emit(controller.getCurrentPosition())
                    delay(1000)
                }
            }

            PlaybackPositionSource.PlayerOnce -> {
                emit(controller.getCurrentPosition())
            }
        }
    }
}



