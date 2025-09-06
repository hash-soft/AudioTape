package com.hashsoft.audiotape.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


class TapeViewModel(
    audioTapeRepository: AudioTapeRepository
) :
    ViewModel() {

    val uiState: StateFlow<TapeUiState> =
        audioTapeRepository.getAll().map {
            TapeUiState.Success(it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TapeUiState.Loading
        )
}

sealed interface TapeUiState {
    data object Loading : TapeUiState
    data class Success(
        val audioTapeList: List<AudioTapeDto>
    ) : TapeUiState
}