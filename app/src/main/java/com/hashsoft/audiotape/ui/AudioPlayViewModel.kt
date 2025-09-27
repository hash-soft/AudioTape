package com.hashsoft.audiotape.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioItemListRepository
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.StorageItemDto
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AudioPlayViewModel(
    private val _audioTape: AudioTapeDto,
    private val _audioTapeRepository: AudioTapeRepository
) :
    ViewModel() {

    private val _audioItemListRepository: AudioItemListRepository =
        AudioItemListRepository(_audioTape.folderPath)

    //val uiState: MutableState<AudioPlayUiState> = mutableStateOf(AudioPlayUiState.Loading)

    val uiState: StateFlow<AudioPlayUiState> =
        _audioTapeRepository.findByPath(_audioTape.folderPath).map {
            val audioItemList = when (val value = uiState.value) {
                is AudioPlayUiState.Success -> value.audioItemList
                else -> _audioItemListRepository.getAudioItemList()
            }
            AudioPlayUiState.Success(audioItemList, it.currentName, it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AudioPlayUiState.Loading
        )

    init {
        viewModelScope.launch {
            _audioTapeRepository.insertAll(_audioTape)
            //val audioItemList = _audioItemListRepository.getAudioItemList()
            //uiState.value = AudioPlayUiState.Success(audioItemList, _audioTape.currentName)
        }
    }

}

// 必要リポジトリ
// オーディオリスト
// 再生設定
// ・再生中の曲
// ・再生中の曲のリジューム時間
// ・再生中の曲の再生状態
// ひとまずこんなものだがDBからの取得とメモリから渡すパターンがある
// かと思ったが事前に読み込んでるからメモリから渡すパターン一択か
// いや、オーディオリストだけは取得するから待ちが発生するかと思ったが同期だから待たないか

sealed interface AudioPlayUiState {
    data object Loading : AudioPlayUiState
    data class Success(
        val audioItemList: List<StorageItemDto>,
        val currentPlay: String,
        val audioTape: AudioTapeDto

    ) : AudioPlayUiState
}