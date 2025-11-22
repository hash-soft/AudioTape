package com.hashsoft.audiotape

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hashsoft.audiotape.data.ThemeMode
import com.hashsoft.audiotape.ui.ThemeModeViewModel
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme

/**
 * アプリのメインコンテンツ
 *
 * @param themeModeViewModel
 */
@Composable
fun MainContent(themeModeViewModel: ThemeModeViewModel = hiltViewModel()) {
    val uiTheme by themeModeViewModel.themeState.collectAsStateWithLifecycle()
    MainScreen(uiTheme)
}

/**
 * アプリのメイン画面
 *
 * @param themeMode
 */
@Composable
private fun MainScreen(themeMode: ThemeMode = ThemeMode.SYSTEM) {
    val darkMode =
        when (themeMode) {
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
        }

    AudioTapeTheme(darkMode) {
        Permission()
    }

}
