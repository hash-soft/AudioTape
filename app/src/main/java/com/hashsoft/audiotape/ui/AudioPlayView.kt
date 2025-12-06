package com.hashsoft.audiotape.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeSortOrder
import com.hashsoft.audiotape.data.ControllerState
import com.hashsoft.audiotape.data.PlayPitchValues
import com.hashsoft.audiotape.data.PlaySpeedValues
import com.hashsoft.audiotape.data.PlayVolumeValues
import com.hashsoft.audiotape.ui.Button.PlayPauseButton
import com.hashsoft.audiotape.ui.dropdown.AudioDropDown
import com.hashsoft.audiotape.ui.dropdown.TextDropdownSelector
import com.hashsoft.audiotape.ui.item.PlaySliderItem
import com.hashsoft.audiotape.ui.theme.IconMedium


/**
 * オーディオ再生ビュー
 *
 * @param playItem 再生アイテム
 * @param playList 再生リスト
 * @param onChangeTapeSettings テープ設定変更時のコールバック
 */
@Composable
fun AudioPlayView(
    contentPosition: Long,
    tape: AudioTapeDto,
    playList: List<AudioItemDto>,
    controllerState: ControllerState,
    onAudioItemClick: (AudioCallbackArgument) -> Unit = {},
    onChangeTapeSettings: (TapeSettingsCallbackArgument) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = tape.currentName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            AudioListDropdownSelector(
                playList,
                tape.currentName,
                onItemSelected = onAudioItemClick
            )
        }

        PlaySliderItem(
            controllerState.isPlaying,
            true,
            contentPosition = if (contentPosition >= 0) contentPosition else tape.position,
            playList.find { it.name == tape.currentName }?.metadata?.duration ?: 0,
        ) {
            onAudioItemClick(AudioCallbackArgument.SeekTo(it))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onAudioItemClick(AudioCallbackArgument.SkipPrevious) }) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = null,
                )
            }
            IconButton(onClick = { onAudioItemClick(AudioCallbackArgument.BackIncrement) }) {
                Icon(
                    imageVector = Icons.Default.FastRewind,
                    contentDescription = null,
                )
            }
            PlayPauseButton(controllerState.isPlaying, true, Modifier.size(IconMedium)) {
                onAudioItemClick(AudioCallbackArgument.PlayPause(it))
            }
            IconButton(onClick = { onAudioItemClick(AudioCallbackArgument.ForwardIncrement) }) {
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = null,
                )
            }
            IconButton(onClick = { onAudioItemClick(AudioCallbackArgument.SkipNext) }) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = null,
                )
            }
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
    playList: List<AudioItemDto>,
    targetName: String = "",
    onItemSelected: (AudioCallbackArgument) -> Unit = {}
) {
    val expanded = remember { mutableStateOf(false) }

    AudioDropDown(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = it },
        trigger = {
            IconButton(onClick = { expanded.value = true }) {
                Icon(
                    Icons.AutoMirrored.Filled.ListAlt,
                    contentDescription = stringResource(R.string.list_description)
                )
            }
        },
        audioItemList = playList,
        targetName = targetName
    ) { index, lastCurrent ->
        if (!lastCurrent) {
            val audioItem = playList.getOrNull(index) ?: return@AudioDropDown
            onItemSelected(AudioCallbackArgument.AudioSelected(index, audioItem.name, 0))
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
            onSortChange(TapeSettingsCallbackArgument.SortOrder(AudioTapeSortOrder.fromInt(it)))
        }
    }
}
