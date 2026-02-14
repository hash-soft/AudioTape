package com.hashsoft.audiotape.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.data.PlayPitchValues
import com.hashsoft.audiotape.data.PlaySpeedValues
import com.hashsoft.audiotape.data.PlayVolumeValues
import com.hashsoft.audiotape.data.UserSettingsDto
import com.hashsoft.audiotape.ui.dialog.SelectSettingDialog
import com.hashsoft.audiotape.ui.item.ClickableSettingItem
import com.hashsoft.audiotape.ui.item.LabelItem
import com.hashsoft.audiotape.ui.item.SwitchItem
import com.hashsoft.audiotape.ui.resource.displayPitchValue
import com.hashsoft.audiotape.ui.resource.displaySpeedValue
import com.hashsoft.audiotape.ui.resource.displayVolumeValue
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme
import com.hashsoft.audiotape.ui.theme.SettingSpacerHeight

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
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        LabelItem(stringResource(R.string.appearance_label))
        ThemeSettingItem(
            userSettings.themeMode.ordinal,
            onSettingChange = onSettingChange
        )

        Spacer(modifier = Modifier.height(SettingSpacerHeight))
        LabelItem(stringResource(R.string.default_settings_label))
        DefaultSortOrderItem(
            userSettings.defaultSortOrder.ordinal,
            onSettingChange = onSettingChange
        )
        SwitchItem(
            stringResource(R.string.repeat_description),
            userSettings.defaultRepeat,
            onCheckedChange = { onSettingChange(UserSettingsCallbackArgument.Repeat(it)) }
        )
        DefaultVolumeItem(
            userSettings.defaultVolume,
            onSettingChange = onSettingChange
        )
        DefaultSpeedItem(
            userSettings.defaultSpeed,
            onSettingChange = onSettingChange
        )
        DefaultPitchItem(
            userSettings.defaultPitch,
            onSettingChange = onSettingChange
        )

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
    modifier: Modifier = Modifier,
    onSettingChange: (argument: UserSettingsCallbackArgument) -> Unit = {},
) {
    val showDialog = remember { mutableStateOf(false) }
    val title = stringResource(R.string.theme_title)
    val themeLabels = stringArrayResource(R.array.theme_labels).toList()

    ClickableSettingItem(
        title = title,
        value = themeLabels.getOrElse(selectedIndex) { "" },
        onClick = { showDialog.value = true },
        modifier = modifier
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


@Composable
private fun DefaultSortOrderItem(
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    onSettingChange: (argument: UserSettingsCallbackArgument) -> Unit = {}
) {
    val showDialog = remember { mutableStateOf(false) }
    val title = stringResource(R.string.sort_order_title)
    val labels = stringArrayResource(R.array.audio_list_sort_labels).toList()

    ClickableSettingItem(
        title = title,
        value = labels.getOrElse(selectedIndex) { "" },
        onClick = { showDialog.value = true },
        modifier = modifier
    )

    if (showDialog.value) {
        SelectSettingDialog(
            title = title,
            options = labels,
            selectedIndex = selectedIndex,
            onSelect = {
                showDialog.value = false
                if (it == selectedIndex) return@SelectSettingDialog
                onSettingChange(UserSettingsCallbackArgument.SortOrder(it))
            },
            onDismissRequest = { showDialog.value = false }
        )

    }
}

@Composable
private fun DefaultVolumeItem(
    volume: Float,
    modifier: Modifier = Modifier,
    onSettingChange: (argument: UserSettingsCallbackArgument) -> Unit = {}
) {
    val showDialog = remember { mutableStateOf(false) }
    val title = stringResource(R.string.volume_title)
    val (index, selectedLabel, labels) = displayVolumeValue(volume)

    ClickableSettingItem(
        title = title,
        value = selectedLabel,
        onClick = { showDialog.value = true },
        modifier = modifier
    )

    if (showDialog.value) {
        SelectSettingDialog(
            title = title,
            options = labels,
            selectedIndex = index,
            onSelect = {
                showDialog.value = false
                if (it == index) return@SelectSettingDialog
                onSettingChange(UserSettingsCallbackArgument.Volume(PlayVolumeValues[it]))
            },
            onDismissRequest = { showDialog.value = false }
        )

    }
}

@Composable
private fun DefaultSpeedItem(
    speed: Float,
    modifier: Modifier = Modifier,
    onSettingChange: (argument: UserSettingsCallbackArgument) -> Unit = {}
) {
    val showDialog = remember { mutableStateOf(false) }
    val title = stringResource(R.string.speed_title)
    val (index, selectedLabel, labels) = displaySpeedValue(speed)

    ClickableSettingItem(
        title = title,
        value = selectedLabel,
        onClick = { showDialog.value = true },
        modifier = modifier
    )

    if (showDialog.value) {
        SelectSettingDialog(
            title = title,
            options = labels,
            selectedIndex = index,
            onSelect = {
                showDialog.value = false
                if (it == index) return@SelectSettingDialog
                onSettingChange(UserSettingsCallbackArgument.Speed(PlaySpeedValues[it]))
            },
            onDismissRequest = { showDialog.value = false }
        )

    }
}

@Composable
private fun DefaultPitchItem(
    pitch: Float,
    modifier: Modifier = Modifier,
    onSettingChange: (argument: UserSettingsCallbackArgument) -> Unit = {}
) {
    val showDialog = remember { mutableStateOf(false) }
    val title = stringResource(R.string.pitch_title)
    val (index, selectedLabel, labels) = displayPitchValue(pitch)

    ClickableSettingItem(
        title = title,
        value = selectedLabel,
        onClick = { showDialog.value = true },
        modifier = modifier
    )

    if (showDialog.value) {
        SelectSettingDialog(
            title = title,
            options = labels,
            selectedIndex = index,
            onSelect = {
                showDialog.value = false
                if (it == index) return@SelectSettingDialog
                onSettingChange(UserSettingsCallbackArgument.Pitch(PlayPitchValues[it]))
            },
            onDismissRequest = { showDialog.value = false }
        )

    }
}


@Preview(showBackground = true)
@Composable
fun UserSettingsViewPreview() {
    AudioTapeTheme {
        UserSettingsView(UserSettingsDto(1))
    }
}