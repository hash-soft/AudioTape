package com.hashsoft.audiotape

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashsoft.audiotape.data.ThemeMode
import com.hashsoft.audiotape.ui.ThemeModeUiState
import com.hashsoft.audiotape.ui.ThemeModeViewModel
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme

@Composable
fun MainContent(themeModeViewModel: ThemeModeViewModel = viewModel(factory = ThemeModeViewModel.Factory)) {
    val uiState by themeModeViewModel.uiState.collectAsStateWithLifecycle()
    MainScreen(uiState)
}

@Composable
private fun MainScreen(uiState: ThemeModeUiState) {
    if (uiState is ThemeModeUiState.Success) {
        val darkMode =
            when (uiState.themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

        AudioTapeTheme(darkMode) {
            Permission()
        }
    }

}