package com.hashsoft.audiotape.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hashsoft.audiotape.AudioTape
import com.hashsoft.audiotape.data.ThemeMode
import com.hashsoft.audiotape.data.UserSettingsDto
import com.hashsoft.audiotape.data.UserSettingsRepository
import com.hashsoft.audiotape.data.UserSettingsRepository.Companion.DEFAULT_ID
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserSettingsViewModel(private val _userSettingsRepository: UserSettingsRepository) :
    ViewModel() {

    val uiState: StateFlow<UserSettingsUiState> =
        _userSettingsRepository.findById(DEFAULT_ID).map { userSettings ->
            if (userSettings == null) {
                UserSettingsUiState.Loading
            } else {
                UserSettingsUiState.Success(userSettings)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettingsUiState.Loading
        )

    fun updateThemeMode(themeMode: ThemeMode) = viewModelScope.launch {
        _userSettingsRepository.updateThemeMode(DEFAULT_ID, themeMode)
    }

    fun updateScreenRestore(screenRestore: Boolean) = viewModelScope.launch {
        _userSettingsRepository.updateScreenRestore(DEFAULT_ID, screenRestore)
    }

    fun updateRewindingSpeed(rewindingSpeed: Float) = viewModelScope.launch {
        _userSettingsRepository.updateRewindingSpeed(DEFAULT_ID, rewindingSpeed)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AudioTape)
                UserSettingsViewModel(application.databaseContainer.userSettingsRepository)
            }
        }
    }
}

sealed interface UserSettingsUiState {
    data object Loading : UserSettingsUiState
    data class Success(
        val userSettings: UserSettingsDto
    ) : UserSettingsUiState
}
