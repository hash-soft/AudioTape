package com.hashsoft.audiotape.ui

import androidx.collection.IntSet
import androidx.collection.intSetOf
import androidx.collection.mutableIntSetOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.FolderStateRepository
import com.hashsoft.audiotape.data.LibraryStateRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.StorageVolumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File


@HiltViewModel
class TapeViewModel @Inject constructor(
    private val _controller: AudioController,
    audioTapeRepository: AudioTapeRepository,
    private val _playingStateRepository: PlayingStateRepository,
    private val _audioTapeRepository: AudioTapeRepository,
    private val _folderStateRepository: FolderStateRepository,
    private val _audioStoreRepository: AudioStoreRepository,
    storageVolumeRepository: StorageVolumeRepository,
    libraryStateRepository: LibraryStateRepository
) :
    ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _baseState =
        libraryStateRepository.tapeListSortOrderFlow().flatMapLatest { sortOrder ->
            combine(
                storageVolumeRepository.volumeChangeFlow(),
                audioTapeRepository.getAll(sortOrder)
            ) { volumes, list ->
                list.map { audioTape ->
                    val treeList =
                        AudioStoreRepository.pathToTreeList(volumes, audioTape.folderPath)
                    audioTape to treeList
                }
            }
        }

    val displayTapeListState =
        combine(_baseState, _playingStateRepository.playingStateFlow()) { list, playingState ->
            list.map {
                val audioTape = it.first
                val isCurrent = playingState.folderPath == audioTape.folderPath
                DisplayTapeItem(audioTape, it.second, isCurrent)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _deleteIdsSet = MutableStateFlow(intSetOf())
    val deleteIdsSet = _deleteIdsSet.asStateFlow()

    fun switchPlayingFolder(tape: AudioTapeDto) {
        val file = _controller.getCurrentMediaItemUri()?.run {
            File(_audioStoreRepository.uriToPath(this))
        }
        if (file?.parent == tape.folderPath) {
            // 同じテープの場合は継続
            return
        }
        val prevPosition = _controller.getCurrentPosition()
        _controller.clearMediaItems()
        viewModelScope.launch {
            _playingStateRepository.saveFolderPath(tape.folderPath)
            if (file != null) {
                _audioTapeRepository.updatePlayingPosition(
                    file.parent ?: "",
                    file.name,
                    prevPosition,
                    false
                )
            }
        }
    }


    fun playWhenReady(playWhenReady: Boolean) = _controller.playWhenReady(playWhenReady)

    fun setPlayingParameters(audioTape: AudioTapeDto) {
        _controller.setRepeat(audioTape.repeat)
        _controller.setVolume(audioTape.volume)
        _controller.setPlaybackParameters(audioTape.speed, audioTape.pitch)
    }

    fun saveSelectedPath(path: String) = viewModelScope.launch {
        _folderStateRepository.saveSelectedPath(path)
    }

    fun addDeleteId(id: Int) {
        _deleteIdsSet.update { currentSet ->
            mutableIntSetOf().apply {
                addAll(currentSet)
                add(id)
            }
        }
    }

    fun removeDeleteId(id: Int) {
        _deleteIdsSet.update { currentSet ->
            mutableIntSetOf().apply {
                addAll(currentSet)
                remove(id)
            }
        }
    }

    fun resetDeleteIds() {
        _deleteIdsSet.update { intSetOf() }
    }

    fun setDeletedIds(ids: IntSet) {
        _deleteIdsSet.update { ids }
    }

    fun deleteSelectedTape(onDeletedAfter: () -> Unit) = viewModelScope.launch {
        val list: MutableList<AudioTapeDto> = mutableListOf()
        _deleteIdsSet.value.forEach { id ->
            val tape = displayTapeListState.value.getOrNull(id) ?: return@forEach
            list.add(tape.audioTape)
        }
        _audioTapeRepository.deleteTapes(list)
        _deleteIdsSet.update { intSetOf() }
        onDeletedAfter()
    }
}

data class DisplayTapeItem(
    val audioTape: AudioTapeDto = AudioTapeDto("", ""),
    val treeList: List<String>? = null,
    val isCurrent: Boolean = false
)
