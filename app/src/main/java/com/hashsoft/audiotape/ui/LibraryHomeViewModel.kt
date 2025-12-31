package com.hashsoft.audiotape.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioTapeListSortOrder
import com.hashsoft.audiotape.data.LibraryStateDto
import com.hashsoft.audiotape.data.LibraryStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class LibraryHomeViewModel @Inject constructor(
    private val _libraryStateRepository: LibraryStateRepository,
) :
    ViewModel() {

    val uiState: StateFlow<LibraryHomeUiState> = _libraryStateRepository.libraryStateFlow().map {
        LibraryHomeUiState.Success(it)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        LibraryHomeUiState.Loading
    )

    private val _existTape = MutableStateFlow(false)
    val existTape: StateFlow<Boolean> = _existTape.asStateFlow()

    private val _viewMode = MutableStateFlow(LibraryHomeViewMode.Normal)
    val viewMode: StateFlow<LibraryHomeViewMode> = _viewMode.asStateFlow()

    fun tabs() = _libraryStateRepository.tabs()

    fun selectedTabIndex(): Int {
        return when (val uiState = uiState.value) {
            is LibraryHomeUiState.Loading -> -1
            is LibraryHomeUiState.Success -> uiState.libraryState.selectedTabIndex
        }
    }

    fun saveSelectedTabName(index: Int) = viewModelScope.launch {
        _libraryStateRepository.saveSelectedTabName(index)
    }

    fun saveTapeListSortOrder(sortOrder: AudioTapeListSortOrder) = viewModelScope.launch {
        _libraryStateRepository.saveTapeListSortOrder(sortOrder)
    }

    fun updateExistTape(exist: Boolean) {
        _existTape.value = exist
    }

    fun updateViewMode(viewMode: LibraryHomeViewMode) {
        _viewMode.value = viewMode
    }

    fun resetViewMode() {
        _viewMode.value = LibraryHomeViewMode.Normal
    }

}

sealed interface LibraryHomeUiState {
    data object Loading : LibraryHomeUiState
    data class Success(
        val libraryState: LibraryStateDto
    ) : LibraryHomeUiState
}

enum class LibraryHomeViewMode {
    Normal,
    DeleteTape
}