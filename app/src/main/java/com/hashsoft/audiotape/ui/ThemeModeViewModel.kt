package com.hashsoft.audiotape.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hashsoft.audiotape.AudioTape
import com.hashsoft.audiotape.data.ThemeMode
import com.hashsoft.audiotape.data.UserSettingsRepository
import com.hashsoft.audiotape.data.UserSettingsRepository.Companion.DEFAULT_ID
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeModeViewModel(private val _userSettingsRepository: UserSettingsRepository) :
    ViewModel() {

    val uiState: StateFlow<ThemeModeUiState> =
        _userSettingsRepository.getThemeMode(DEFAULT_ID).map { themeMode ->
            if (themeMode == null) {
                ThemeModeUiState.Loading
            } else {
                ThemeModeUiState.Success(themeMode)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeModeUiState.Loading
        )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AudioTape)
                ThemeModeViewModel(application.databaseContainer.userSettingsRepository)
            }
        }
    }
}

sealed interface ThemeModeUiState {
    data object Loading : ThemeModeUiState
    data class Success(
        val themeMode: ThemeMode
    ) : ThemeModeUiState
}