package com.hashsoft.audiotape.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.data.AudioTapeSortOrder
import com.hashsoft.audiotape.data.DisplayStorageItem
import com.hashsoft.audiotape.data.PlayAudioDto
import com.hashsoft.audiotape.ui.dropdown.AudioDropDown
import com.hashsoft.audiotape.ui.dropdown.TextDropdownSelector

/**
 * 再生音量の値リスト
 */
private val PlayVolumeValues: List<Float> =
    listOf(0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f)

/**
 * 再生速度の値リスト
 */
private val PlaySpeedValues: List<Float> =
    listOf(0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f)

/**
 * 再生ピッチの値リスト
 */
private val PlayPitchValues: List<Float> = listOf(
    0.5f,
    0.52973f,
    0.56123f,
    0.59460f,
    0.62996f,
    0.66742f,
    0.70711f,
    0.74915f,
    0.79370f,
    0.84090f,
    0.89090f,
    0.94387f,
    1.0f,
    1.05946f,
    1.12246f,
    1.18921f,
    1.25992f,
    1.33484f,
    1.41421f,
    1.49831f,
    1.58740f,
    1.68179f,
    1.78180f,
    1.88775f,
    2.0f
)

/**
 * オーディオ再生ビュー
 *
 * @param playItem 再生アイテム
 * @param playList 再生リスト
 * @param onChangeTapeSettings テープ設定変更時のコールバック
 */
@Composable
fun AudioPlayView(
    playItem: PlayAudioDto,
    playList: List<DisplayStorageItem<AudioItemDto>>,
    onChangeTapeSettings: (TapeSettingsCallbackArgument) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tape = playItem.audioTape
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
            AudioListDropdownSelector(playList, onChangeTapeSettings)
            SortDropdownSelector(tape.sortOrder, onChangeTapeSettings)
        }
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
    val volumeLabels = stringArrayResource(R.array.play_volume_labels).toList()

    val index = PlayVolumeValues.indexOf(volume)
    val selectedLabel =
        if (index < 0) stringResource(
            R.string.not_found_volume_label,
            volume
        ) else volumeLabels[index]

    TextDropdownSelector(
        volumeLabels,
        title,
        selectedIndex = index,
        iconContent = { Text(selectedLabel) }) {
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
    val speedLabels = stringArrayResource(R.array.play_speed_labels).toList()

    val index = PlaySpeedValues.indexOf(speed)
    val selectedLabel =
        if (index < 0) stringResource(R.string.not_found_speed_label, speed) else speedLabels[index]

    TextDropdownSelector(
        speedLabels,
        title,
        selectedIndex = index,
        iconContent = { Text(selectedLabel) }) {
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
    val pitchLabels = stringArrayResource(R.array.play_pitch_labels).toList()

    val index = PlayPitchValues.indexOf(pitch)
    val selectedLabel =
        if (index < 0) stringResource(R.string.not_found_pitch_label, pitch) else pitchLabels[index]

    TextDropdownSelector(
        pitchLabels,
        title,
        selectedIndex = index,
        iconContent = { Text(selectedLabel) }) {
        if (index != it) {
            onPitchChange(TapeSettingsCallbackArgument.Pitch(PlayPitchValues[it]))
        }
    }
}


/**
 * オーディオリストドロップダウンセレクター
 *
 * @param playList 再生リスト
 * @param onItemSelected アイテム選択時のコールバック
 */
@Composable
private fun AudioListDropdownSelector(
    playList: List<DisplayStorageItem<AudioItemDto>>,
    onItemSelected: (TapeSettingsCallbackArgument) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    AudioDropDown(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        trigger = {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    Icons.AutoMirrored.Filled.ListAlt,
                    contentDescription = stringResource(R.string.list_description)
                )
            }
        },
        audioItemList = playList
    ) { index, lastCurrent ->
        if (!lastCurrent) {
            onItemSelected(TapeSettingsCallbackArgument.ItemSelected(index))
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

    TextDropdownSelector(sortLabels, title, selectedIndex = index, iconContent = {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Sort,
            contentDescription = stringResource(R.string.sort_order_description),
        )
    }) {
        if (index != it) {
            onSortChange(TapeSettingsCallbackArgument.SortOrder(it))
        }
    }
}
