package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeSortOrder
import com.hashsoft.audiotape.data.PlayPitchValues
import com.hashsoft.audiotape.data.PlaySpeedValues
import com.hashsoft.audiotape.data.PlayVolumeValues
import com.hashsoft.audiotape.ui.TapeSettingsCallbackArgument
import com.hashsoft.audiotape.ui.dialog.TextPopupSelector
import com.hashsoft.audiotape.ui.resource.displayPitchValue
import com.hashsoft.audiotape.ui.resource.displaySpeedValue
import com.hashsoft.audiotape.ui.resource.displayVolumeValue
import com.hashsoft.audiotape.ui.text.FixedWidthText
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme


@Composable
fun AudioPlaySettingsItem(
    tape: AudioTapeDto,
    modifier: Modifier = Modifier,
    onChangeTapeSettings: (TapeSettingsCallbackArgument) -> Unit = {}
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        VolumeDropdownSelector(tape.volume, onChangeTapeSettings)
        SpeedDropdownSelector(tape.speed, onChangeTapeSettings)
        PitchDropdownSelector(tape.pitch, onChangeTapeSettings)

        IconButton(onClick = { onChangeTapeSettings(TapeSettingsCallbackArgument.Repeat(!tape.repeat)) }) {
            val tint =
                if (tape.repeat) LocalContentColor.current else LocalContentColor.current.copy(
                    alpha = 0.5f
                )
            Icon(
                imageVector = Icons.Default.Repeat,
                contentDescription = stringResource(R.string.repeat_description),
                tint = tint
            )
        }
        SortDropdownSelector(tape.sortOrder, onChangeTapeSettings)
    }
}


/**
 * 音量ドロップダウンセレクター
 *
 * @param volume 音量
 * @param onVolumeChange 音量変更時のコールバック
 */
@Composable
private fun VolumeDropdownSelector(
    volume: Float = 1.0f,
    onVolumeChange: (TapeSettingsCallbackArgument) -> Unit = {}
) {
    val title = stringResource(R.string.volume_title)
    val (index, selectedLabel, volumeLabels) = displayVolumeValue(volume)

    TextPopupSelector(
        volumeLabels,
        title,
        selectedIndex = index,
        nonIconContent = true,
        buttonContent = { FixedWidthText(selectedLabel, 4, sampleChar = "%") }) {
        if (index != it) {
            onVolumeChange(TapeSettingsCallbackArgument.Volume(PlayVolumeValues[it]))
        }
    }
}

/**
 * 再生速度ドロップダウンセレクター
 *
 * @param speed 再生速度
 * @param onSpeedChange 再生速度変更時のコールバック
 */
@Composable
private fun SpeedDropdownSelector(
    speed: Float = 1.0f,
    onSpeedChange: (TapeSettingsCallbackArgument) -> Unit = {}
) {
    val title = stringResource(R.string.speed_title)
    val (index, selectedLabel, speedLabels) = displaySpeedValue(speed)

    TextPopupSelector(
        speedLabels,
        title,
        selectedIndex = index,
        nonIconContent = true,
        buttonContent = { FixedWidthText(selectedLabel, 5, sampleChar = "9") }) {
        if (index != it) {
            onSpeedChange(TapeSettingsCallbackArgument.Speed(PlaySpeedValues[it]))
        }
    }
}

/**
 * ピッチドロップダウンセレクター
 *
 * @param pitch ピッチ
 * @param onPitchChange ピッチ変更時のコールバック
 */
@Composable
private fun PitchDropdownSelector(
    pitch: Float = 1.0f,
    onPitchChange: (TapeSettingsCallbackArgument) -> Unit = {}
) {
    val title = stringResource(R.string.pitch_title)
    val (index, selectedLabel, pitchLabels) = displayPitchValue(pitch)

    TextPopupSelector(
        pitchLabels,
        title,
        selectedIndex = index,
        nonIconContent = true,
        buttonContent = { FixedWidthText(selectedLabel, 4, sampleChar = "#") }) {
        if (index != it) {
            onPitchChange(TapeSettingsCallbackArgument.Pitch(PlayPitchValues[it]))
        }
    }
}


/**
 * ソート順ドロップダウンセレクター
 *
 * @param sortOrder ソート順
 * @param onSortChange ソート順変更時のコールバック
 */
@Composable
private fun SortDropdownSelector(
    sortOrder: AudioTapeSortOrder = AudioTapeSortOrder.NAME_ASC,
    onSortChange: (TapeSettingsCallbackArgument) -> Unit = {}
) {
    val title = stringResource(R.string.sort_order_title)
    val sortLabels = stringArrayResource(R.array.audio_list_sort_labels).toList()
    val index = sortOrder.ordinal

    TextPopupSelector(sortLabels, title, selectedIndex = index, buttonContent = {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Sort,
            contentDescription = stringResource(R.string.sort_order_description),
        )
    }) {
        if (index != it) {
            onSortChange(TapeSettingsCallbackArgument.SortOrder(AudioTapeSortOrder.fromInt(it)))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AudioPlaySettingsItemPreview() {
    AudioTapeTheme {
        AudioPlaySettingsItem(
            tape = AudioTapeDto("folderPath", "currentPath", "currentName")
        )
    }
}