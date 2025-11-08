package com.hashsoft.audiotape.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.data.UserSettingsDto
import com.hashsoft.audiotape.ui.dialog.SelectSettingDialog
import com.hashsoft.audiotape.ui.item.ClickableSettingItem

/**
 * ユーザー設定画面のView
 *
 * @param userSettings ユーザー設定
 * @param onSettingChange 設定変更時のコールバック
 */
@Composable
fun UserSettingsView(
    userSettings: UserSettingsDto,
    onSettingChange: (argument: UserSettingsCallbackArgument) -> Unit = {}
) {
    Column {
        ThemeSettingItem(userSettings.themeMode.ordinal, onSettingChange)
    }

}

/**
 * テーマ設定項目
 *
 * @param selectedIndex 選択中のインデックス
 * @param onSettingChange 設定変更時のコールバック
 */
@Composable
private fun ThemeSettingItem(
    selectedIndex: Int,
    onSettingChange: (argument: UserSettingsCallbackArgument) -> Unit = {}
) {
    val showDialog = remember { mutableStateOf(false) }
    val title = stringResource(R.string.theme_title)
    val themeLabels = stringArrayResource(R.array.theme_labels).toList()

    ClickableSettingItem(
        title = title,
        value = themeLabels.getOrElse(selectedIndex) { "" },
        onClick = { showDialog.value = true }
    )

    if (showDialog.value) {
        SelectSettingDialog(
            title = title,
            options = themeLabels,
            selectedIndex = selectedIndex,
            onSelect = {
                showDialog.value = false
                if (it == selectedIndex) return@SelectSettingDialog
                onSettingChange(UserSettingsCallbackArgument.Theme(it))
            },
            onDismissRequest = { showDialog.value = false }
        )

    }
}
