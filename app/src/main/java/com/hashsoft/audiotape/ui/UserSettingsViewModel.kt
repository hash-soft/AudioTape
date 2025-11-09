package com.hashsoft.audiotape.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.ThemeMode
import com.hashsoft.audiotape.data.UserSettingsDto
import com.hashsoft.audiotape.data.UserSettingsRepository
import com.hashsoft.audiotape.data.UserSettingsRepository.Companion.DEFAULT_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class UserSettingsViewModel @Inject constructor(private val _userSettingsRepository: UserSettingsRepository) :
    ViewModel() {

    val uiState: StateFlow<UserSettingsUiState> =
        _userSettingsRepository.findById(DEFAULT_ID).map { userSettings ->
            if (userSettings == null) {
                UserSettingsUiState.Creating
            } else {
                UserSettingsUiState.Success(userSettings)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettingsUiState.Loading
        )

    fun insertUserSettings() = viewModelScope.launch {
        _userSettingsRepository.insertAll(UserSettingsDto(uid = DEFAULT_ID))
    }

    fun updateThemeMode(themeMode: ThemeMode) = viewModelScope.launch {
        _userSettingsRepository.updateThemeMode(DEFAULT_ID, themeMode)
    }

}

sealed interface UserSettingsUiState {
    data object Loading : UserSettingsUiState
    data object Creating : UserSettingsUiState
    data class Success(
        val userSettings: UserSettingsDto
    ) : UserSettingsUiState
}
