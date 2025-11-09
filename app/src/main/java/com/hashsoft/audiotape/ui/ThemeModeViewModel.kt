package com.hashsoft.audiotape.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.ThemeMode
import com.hashsoft.audiotape.data.UserSettingsRepository
import com.hashsoft.audiotape.data.UserSettingsRepository.Companion.DEFAULT_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * テーマモードのViewModel
 *
 * @param _userSettingsRepository ユーザー設定リポジトリ
 */
@HiltViewModel
class ThemeModeViewModel @Inject constructor(private val _userSettingsRepository: UserSettingsRepository) :
    ViewModel() {

    /**
     * テーマモードのUI状態
     */
    val uiState: StateFlow<ThemeModeUiState> =
        _userSettingsRepository.getThemeMode(DEFAULT_ID).map { themeMode ->
            ThemeModeUiState.Success(themeMode)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeModeUiState.Loading
        )
}

/**
 * テーマモードのUI状態
 */
sealed interface ThemeModeUiState {
    /**
     * 読み込み中
     */
    data object Loading : ThemeModeUiState

    /**
     * 成功
     *
     * @param themeMode テーマモード
     */
    data class Success(
        val themeMode: ThemeMode
    ) : ThemeModeUiState
}
