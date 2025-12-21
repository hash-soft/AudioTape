package com.hashsoft.audiotape.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.AudioTapeSortOrder
import com.hashsoft.audiotape.data.ControllerRepository
import com.hashsoft.audiotape.data.DisplayFolder
import com.hashsoft.audiotape.data.DisplayPlayingSource
import com.hashsoft.audiotape.data.FolderStateRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.StorageAddressUseCase
import com.hashsoft.audiotape.data.StorageItem
import com.hashsoft.audiotape.data.StorageItemListUseCase
import com.hashsoft.audiotape.data.StorageItemListUseCase.Companion.sortedAudioList
import com.hashsoft.audiotape.data.StorageItemListUseCase.Companion.sortedFolderList
import com.hashsoft.audiotape.data.StorageVolumeRepository
import com.hashsoft.audiotape.data.UserSettingsDto
import com.hashsoft.audiotape.data.UserSettingsRepository
import com.hashsoft.audiotape.logic.PlaybackHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

enum class FolderViewState {
    Start,
    ItemLoading,
    Success,
}

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val _controller: AudioController,
    private val _folderStateRepository: FolderStateRepository,
    storageAddressUseCase: StorageAddressUseCase,
    storageItemListUseCase: StorageItemListUseCase,
    private val _audioTapeRepository: AudioTapeRepository,
    private val _playingStateRepository: PlayingStateRepository,
    private val _controllerPlayingRepository: ControllerRepository,
    storageVolumeRepository: StorageVolumeRepository,
    private val _audioStoreRepository: AudioStoreRepository,
    userSettingsRepository: UserSettingsRepository
) :
    ViewModel() {

    private val _state = MutableStateFlow(FolderViewState.Start)
    val state: StateFlow<FolderViewState> = _state.asStateFlow()

    val addressBarState = AddressBarState(storageAddressUseCase)


    private val _baseState = combine(
        storageVolumeRepository.volumeChangeFlow(),
        _audioStoreRepository.updateFlow,
        _folderStateRepository.folderStateFlow()
    ) { volumes, _, folderState ->
        _state.update { FolderViewState.ItemLoading }
        addressBarState.load(folderState.selectedPath, volumes)
        val listPair =
            storageItemListUseCase.pathToStorageItemList(volumes, folderState.selectedPath, null)
        folderState.selectedPath to listPair
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _listState = _baseState.flatMapLatest { pair ->
        combine(
            _audioTapeRepository.findSortOrderByPath(pair.first).distinctUntilChanged(),
            userSettingsRepository.findDefaultSortOrderById(UserSettingsRepository.DEFAULT_ID)
                .distinctUntilChanged()
        ) { (tapeSortOrder, defaultSortOrder) ->
            tapeSortOrder ?: defaultSortOrder ?: AudioTapeSortOrder.NAME_ASC
        }.distinctUntilChanged().map { sortOrder ->
            val listPair = pair.second
            val folderList = sortedFolderList(listPair.first, sortOrder)
            val audioList = sortedAudioList(listPair.second, sortOrder)
            val expandIndexList =
                folderList.indices.toList() + audioList.indices.toList()
            pair.first to Pair(folderList + audioList, expandIndexList)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val displayFolderState = _listState.flatMapLatest { (folderPath, listPair) ->
        combine(
            _audioTapeRepository.findByPath(folderPath),
            userSettingsRepository.findById(UserSettingsRepository.DEFAULT_ID),
            _controllerPlayingRepository.playbackStatus.map {
                val displayPlaying = PlaybackHelper.playbackStatusToDisplayPlayingSource(it)
                displayPlaying != DisplayPlayingSource.Pause
            }.distinctUntilChanged(),
            _playingStateRepository.playingStateFlow()
        ) { audioTape, settings, isPlaying, playingState ->
            _state.update { FolderViewState.Success }
            DisplayFolder(
                folderPath = folderPath,
                listPair.first,
                listPair.second,
                audioTape = audioTape,
                playingState,
                settings ?: UserSettingsDto(UserSettingsRepository.DEFAULT_ID),
                isPlaying
            )
        }
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), DisplayFolder()
    )

    val availableState = _controller.availableStateFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )


    fun saveSelectedPath(path: String) = viewModelScope.launch {
        _folderStateRepository.saveSelectedPath(path)
    }

    fun setCurrentMediaItemsPosition(list: List<StorageItem>, index: Int, position: Long): Boolean {
        // ファイルを抽出する
        val audioList = list.mapNotNull {
            when (it) {
                is AudioItemDto -> it

                else -> return@mapNotNull null
            }
        }
        val audioItem = audioList.getOrNull(index) ?: return true
        if (_controller.isCurrentById(audioItem.id)) {
            return true
        }
        if (_controller.seekToById(audioItem.id, position)) {
            return true
        }

        return false
    }

    fun switchPlayingFolder(audioTape: AudioTapeDto, create: Boolean) {
        // MediaItemの変更前に通知がこないので前のcurrentの位置を更新する
        val prevPosition = _controller.getCurrentPosition()
        val prevUri = _controller.getCurrentMediaItemUri()
        _controller.clearMediaItems()
        updatePlaying(audioTape, create, prevUri, prevPosition)
    }

    private fun updatePlaying(
        audioTape: AudioTapeDto,
        create: Boolean,
        prevUri: Uri?,
        prevPosition: Long
    ) {
        viewModelScope.launch {
            _audioTapeRepository.updatePlayingPosition(
                audioTape.folderPath,
                audioTape.currentName,
                audioTape.position,
                false
            )
            if (create) {
                val result = _audioTapeRepository.insertNew(audioTape)
                Timber.d("insertNew result: $result folderPath: ${audioTape.folderPath}")
            }
            _playingStateRepository.saveFolderPath(audioTape.folderPath)
            prevUri?.let { uri ->
                val file = File(_audioStoreRepository.uriToPath(uri))
                _audioTapeRepository.updatePlayingPosition(
                    file.parent ?: "",
                    file.name,
                    prevPosition,
                    false
                )
            }
        }
    }

    fun setPlayingParameters(audioTape: AudioTapeDto) {
        _controller.setRepeat(audioTape.repeat)
        _controller.setPlaybackParameters(audioTape.speed, audioTape.pitch)
        _controller.setVolume(audioTape.volume)
    }

    fun playWhenReady(playWhenReady: Boolean) = _controller.playWhenReady(playWhenReady)

    fun makeAudioTape(
        srcAudioTape: AudioTapeDto?,
        settings: UserSettingsDto,
        folderPath: String,
        currentName: String,
    ): AudioTapeDto {
        return if (srcAudioTape == null) {
            AudioTapeDto(
                folderPath = folderPath,
                currentName = currentName,
                sortOrder = settings.defaultSortOrder,
                repeat = settings.defaultRepeat,
                volume = settings.defaultVolume,
                speed = settings.defaultSpeed,
                pitch = settings.defaultPitch
            )
        } else {
            return if (srcAudioTape.currentName == currentName) {
                srcAudioTape
            } else {
                srcAudioTape.copy(currentName = currentName, position = 0)
            }
        }
    }
}



