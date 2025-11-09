package com.hashsoft.audiotape.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.data.ThemeMode


/**
 * ユーザー設定画面のホーム画面となるコンポーザブル
 *
 * @param onBackClick 戻るボタンクリック時のコールバック
 */
@Composable
fun UserSettingsHomeRoute(
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onBackClick()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            UserSettingsRoute()
        }
    }
}

/**
 * ユーザー設定画面のルートとなるコンポーザブル
 *
 * @param viewModel ユーザー設定画面のViewModel
 */
@Composable
private fun UserSettingsRoute(viewModel: UserSettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is UserSettingsUiState.Loading -> {}
        is UserSettingsUiState.Creating -> {
            viewModel.insertUserSettings()
        }

        is UserSettingsUiState.Success -> {
            UserSettingsView(state.userSettings) {
                userSettings(it, viewModel)
            }
        }
    }
}

/**
 * ユーザー設定のコールバックを処理する
 *
 * @param argument ユーザー設定のコールバック引数
 * @param viewModel ユーザー設定画面のViewModel
 */
private fun userSettings(argument: UserSettingsCallbackArgument, viewModel: UserSettingsViewModel) {
    when (argument) {
        is UserSettingsCallbackArgument.Theme -> {
            viewModel.updateThemeMode(ThemeMode.fromInt(argument.theme))
        }
    }
}
