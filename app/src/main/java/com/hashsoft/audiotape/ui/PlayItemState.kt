package com.hashsoft.audiotape.ui

import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.ControllerRepository
import com.hashsoft.audiotape.data.ItemStatus
import com.hashsoft.audiotape.data.PlaybackPosition
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.StorageItemListUseCase
import com.hashsoft.audiotape.data.StorageVolumeRepository
import com.hashsoft.audiotape.logic.PlaybackHelper
import com.hashsoft.audiotape.logic.StorageHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
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
            Triple(audioTape, sortedList, treeList) to playingState.folderPath
        } else null to playingState.folderPath
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val displayPlayingState = _baseState.flatMapLatest { pair ->
        Timber.d("audioState")
        val triple = pair.first
        if (triple != null) {
            _audioTapeRepository.findByPath(triple.first.folderPath).map { audioTape ->
                Timber.d("tape changed = $audioTape")
                if (audioTape == null) PlayItemStateResult.NoTape(pair.second) else {
                    val sortedList = if (audioTape.sortOrder != triple.first.sortOrder) {
                        // _baseStateからソートが変更されていた場合リストとplayerの更新を行う
                        val result = StorageItemListUseCase.sortedAudioList(
                            triple.second,
                            audioTape.sortOrder
                        )
                        controller.sortMediaItems(result)
                        result
                    } else {
                        // _baseStateからソートが変更されていなかった場合playerだけ更新を行う
                        // リストは_baseState.secondで実行済みだがplayerのほうはif(true)のほうで更新されている
                        // 可能性があるので_baseState.secondに合わせる必要がある
                        controller.sortMediaItems(triple.second)
                        triple.second
                    }
                    val currentAudio = sortedList.find { it.name == audioTape.currentName }
                    PlayItemStateResult.Success(
                        DisplayPlayingItem(
                            audioTape, sortedList, triple.third,
                            currentAudio = currentAudio,
                            status = StorageHelper.checkState(
                                sortedList.size,
                                currentAudio != null,
                                audioTape.folderPath
                            )
                        )
                    )
                }
            }
        } else flowOf(PlayItemStateResult.NoTape(pair.second))

    }

    val availableState =
        combine(_baseState, controller.availableStateFlow) { pair, available ->
            Timber.d("availableState = $available, isPlaying: ${controller.isPlaying}")
            val audio = pair.first
            if (audio != null) {
                if (controller.getMediaItemCount() == 0) {
                    val audioTape = audio.first
                    controller.setMediaItems(
                        audio.second,
                        audioTape.currentName,
                        audioTape.position
                    )
                    controller.prepare()
                    controller.applyAudioProfile(audioTape.volume, audioTape.speed, audioTape.pitch)
                } else {
                    controller.replaceMediaItemsWith(audio.second)
                }
            }
            if (available && controller.isPlaying) {
                controllerRepository.updatePlaybackPosition(PlaybackPosition.Player(0, true))
            }
            available
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentPosition = controllerRepository.playbackPosition.transformLatest { playback ->
        when (playback) {
            is PlaybackPosition.None -> {
                emit(-1L)
            }

            is PlaybackPosition.Player -> {
                if (!playback.skipInitial) {
                    emitPosition(playback.position, this::emit)
                }
                while (currentCoroutineContext().isActive) {
                    emitPosition(controller.getCurrentPosition(), this::emit)
                    delay(1000)
                }
            }

            is PlaybackPosition.Once -> {
                emitPosition(playback.position, this::emit)
            }
        }
    }

    private suspend fun emitPosition(position: Long, emit: suspend (Long) -> Unit) {
        if (position >= 0) {
            emit(position)
        }
    }

    val displayPlayingSource = controllerRepository.playbackStatus.map {
        PlaybackHelper.playbackStatusToDisplayPlayingSource(it)
    }.distinctUntilChanged()
}

data class DisplayPlayingItem(
    val audioTape: AudioTapeDto = AudioTapeDto("", "", ""),
    val audioList: List<AudioItemDto> = listOf(),
    val treeList: List<String>? = null,
    val currentAudio: AudioItemDto? = null,
    val status: ItemStatus = ItemStatus.Normal
)

sealed interface PlayItemStateResult {
    data object Loading : PlayItemStateResult
    data class NoTape(val name: String) : PlayItemStateResult
    data class Success(val displayPlayingItem: DisplayPlayingItem) : PlayItemStateResult
}