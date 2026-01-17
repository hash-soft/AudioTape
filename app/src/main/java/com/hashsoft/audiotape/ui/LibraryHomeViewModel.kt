package com.hashsoft.audiotape.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.LibraryStateDto
import com.hashsoft.audiotape.data.LibraryStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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

}

sealed interface LibraryHomeUiState {
    data object Loading : LibraryHomeUiState
    data class Success(
        val libraryState: LibraryStateDto
    ) : LibraryHomeUiState
}
