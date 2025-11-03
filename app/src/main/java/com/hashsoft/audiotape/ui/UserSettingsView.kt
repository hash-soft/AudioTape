package com.hashsoft.audiotape.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import com.hashsoft.audiotape.data.UserSettingsDto


@Composable
fun UserSettingsView(
    userSettings: UserSettingsDto,
    onSettingChange: (argument: UserSettingsCallbackArgument) -> Unit = {}
) {
    Column {
        Checkbox(
            checked = userSettings.screenRestore,
            onCheckedChange = { checked ->
                onSettingChange(UserSettingsCallbackArgument.Theme(0))
            }
        )
    }
}