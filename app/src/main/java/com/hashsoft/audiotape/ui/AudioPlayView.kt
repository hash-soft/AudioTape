package com.hashsoft.audiotape.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.data.AudioItemMetadata
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeSortOrder
import com.hashsoft.audiotape.data.DisplayPlayingSource
import com.hashsoft.audiotape.data.ItemStatus
import com.hashsoft.audiotape.data.PlayPitchValues
import com.hashsoft.audiotape.data.PlaySpeedValues
import com.hashsoft.audiotape.data.PlayVolumeValues
import com.hashsoft.audiotape.logic.TextHelper
import com.hashsoft.audiotape.ui.button.PlayPauseButton
import com.hashsoft.audiotape.ui.dropdown.AudioDropDown
import com.hashsoft.audiotape.ui.dropdown.TextDropdownSelector
import com.hashsoft.audiotape.ui.item.PlaySliderItem
import com.hashsoft.audiotape.ui.resource.displayPitchValue
import com.hashsoft.audiotape.ui.resource.displaySpeedValue
import com.hashsoft.audiotape.ui.resource.displayVolumeValue
import com.hashsoft.audiotape.ui.text.FixedWidthText
import com.hashsoft.audiotape.ui.text.TappableMarqueeText
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme
import com.hashsoft.audiotape.ui.theme.IconMedium
import com.hashsoft.audiotape.ui.theme.audioPlayFileAlpha


/**
 * オーディオ再生ビュー
 *
 * @param playList 再生リスト
 * @param onChangeTapeSettings テープ設定変更時のコールバック
 */
@Composable
fun AudioPlayView(
    isAvailable: Boolean,
    contentPosition: Long,
    tape: AudioTapeDto,
    playList: List<AudioItemDto>,
    status: ItemStatus,
    displayPlaying: DisplayPlayingSource,
    onAudioItemClick: (AudioCallbackArgument) -> Unit = {},
    onChangeTapeSettings: (TapeSettingsCallbackArgument) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val metadata = playList.find { it.name == tape.currentName }?.metadata
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AudioFile,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
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
            Column(modifier = Modifier.weight(1f)) {
                TappableMarqueeText(
                    text = if (metadata == null) "" else TextHelper.joinNonEmpty(
                        stringResource(R.string.metadata_separator),
                        metadata.artist,
                        metadata.title,
                        metadata.album
                    ),
                    color = Color.Unspecified.copy(alpha = audioPlayFileAlpha(status)),
                    style = MaterialTheme.typography.bodyMedium,
                )
                TappableMarqueeText(
                    text = tape.currentName,
                    color = Color.Unspecified.copy(alpha = audioPlayFileAlpha(status)),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            AudioListDropdownSelector(
                playList,
                tape.currentName,
                enabled = isAvailable && ItemStatus.isPlayable(status),
                onItemSelected = onAudioItemClick
            )
        }

        PlaySliderItem(
            displayPlaying != DisplayPlayingSource.Pause,
            enabled = isAvailable && ItemStatus.isSeekable(status),
            contentPosition = if (contentPosition >= 0) contentPosition else tape.position,
            durationMs = metadata?.duration ?: 0,
            amplitude = tape.volume,
            speed = tape.speed,
        ) {
            onAudioItemClick(AudioCallbackArgument.SeekTo(it))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onAudioItemClick(AudioCallbackArgument.SkipPrevious) },
                enabled = isAvailable && ItemStatus.isSkippable(status)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = null,
                )
            }
            IconButton(
                onClick = { onAudioItemClick(AudioCallbackArgument.BackIncrement) },
                enabled = isAvailable && ItemStatus.isSeekable(status)
            ) {
                Icon(
                    imageVector = Icons.Default.FastRewind,
                    contentDescription = null,
                )
            }
            PlayPauseButton(
                displayPlaying != DisplayPlayingSource.Pause,
                enabled = isAvailable && ItemStatus.isPlayable(status),
                Modifier.size(IconMedium)
            ) {
                onAudioItemClick(AudioCallbackArgument.PlayPause(it))
            }
            IconButton(
                onClick = { onAudioItemClick(AudioCallbackArgument.ForwardIncrement) },
                enabled = isAvailable && ItemStatus.isSeekable(status)
            ) {
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = null,
                )
            }
            IconButton(
                onClick = { onAudioItemClick(AudioCallbackArgument.SkipNext) },
                enabled = isAvailable && ItemStatus.isSkippable(status)
            ) {
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
    val (index, selectedLabel, volumeLabels) = displayVolumeValue(volume)

    TextDropdownSelector(
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

    TextDropdownSelector(
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

    TextDropdownSelector(
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
 * オーディオリストドロップダウンセレクター
 *
 * @param playList 再生リスト
 * @param onItemSelected アイテム選択時のコールバック
 */
@Composable
private fun AudioListDropdownSelector(
    playList: List<AudioItemDto>,
    targetName: String = "",
    enabled: Boolean = true,
    onItemSelected: (AudioCallbackArgument) -> Unit = {}
) {
    val expanded = remember { mutableStateOf(false) }

    AudioDropDown(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = it },
        trigger = {
            IconButton(onClick = { expanded.value = true }, enabled = enabled) {
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

    TextDropdownSelector(sortLabels, title, selectedIndex = index, buttonContent = {
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
fun AudioPlayViewPreview() {
    AudioTapeTheme {
        AudioPlayView(
            isAvailable = true,
            contentPosition = 1000,
            tape = AudioTapeDto("folderPath", "currentPath", "currentName"),
            playList = listOf(
                AudioItemDto(
                    "currentName",
                    "",
                    "",
                    0,
                    0,
                    0,
                    "",
                    AudioItemMetadata("album", "title", "artist", 30000, 0)
                )
            ),
            status = ItemStatus.Normal,
            displayPlaying = DisplayPlayingSource.Pause
        )
    }
}