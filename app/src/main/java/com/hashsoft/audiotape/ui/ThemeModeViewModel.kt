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
    val themeState: StateFlow<ThemeMode> =
        _userSettingsRepository.findThemeModeById(DEFAULT_ID)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ThemeMode.SYSTEM
            )
}
