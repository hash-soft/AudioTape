package com.hashsoft.audiotape.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hashsoft.audiotape.data.UserSettingsDto
import com.hashsoft.audiotape.ui.UserSettingsItem.ScreenRestore

private sealed interface UserSettingsItem {
    data class ScreenRestore(val checked: Boolean) : UserSettingsItem
}


@Composable
fun UserSettingsRoute(userSettingsViewModel: UserSettingsViewModel = hiltViewModel()) {
    val uiState by userSettingsViewModel.uiState.collectAsStateWithLifecycle()
    UserSettingsView(uiState) { item ->
        when (item) {
            is ScreenRestore -> {
                userSettingsViewModel.updateScreenRestore(item.checked)
            }
        }
    }
}

@Composable
private fun UserSettingsView(
    uiState: UserSettingsUiState,
    onSettingChange: (item: UserSettingsItem) -> Unit
) {
    if (uiState is UserSettingsUiState.Success) {
        UserSettings(
            userSettings = uiState.userSettings,
            onSettingChange = onSettingChange
        )
    }
}

@Composable
private fun UserSettings(
    userSettings: UserSettingsDto,
    onSettingChange: (item: UserSettingsItem) -> Unit
) {
    Column {
        Checkbox(
            checked = userSettings.screenRestore,
            onCheckedChange = { checked ->
                onSettingChange(ScreenRestore(checked))
            }
        )
    }
}