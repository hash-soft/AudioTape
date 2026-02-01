package com.hashsoft.audiotape.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.data.AudioItemMetadata
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.DisplayPlayingSource
import com.hashsoft.audiotape.data.ItemStatus
import com.hashsoft.audiotape.ui.item.AudioPlayCurrentItem
import com.hashsoft.audiotape.ui.item.AudioPlayPerformanceItem
import com.hashsoft.audiotape.ui.item.AudioPlaySettingsItem
import com.hashsoft.audiotape.ui.item.PlaySliderItem
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme
import com.hashsoft.audiotape.ui.theme.PreviewOrientation


@Composable
fun AdaptiveAudioPlayView(
    isAvailable: Boolean,
    contentPosition: Long,
    tape: AudioTapeDto,
    playList: List<AudioItemDto>,
    status: ItemStatus,
    displayPlaying: DisplayPlayingSource,
    onAudioItemClick: (AudioCallbackArgument) -> Unit = {},
    onChangeTapeSettings: (TapeSettingsCallbackArgument) -> Unit = {}
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    if (isLandscape) {
        AudioPlayViewPortrait(
            isAvailable,
            contentPosition,
            tape,
            playList,
            status,
            displayPlaying,
            onAudioItemClick,
            onChangeTapeSettings
        )
    } else {
        AudioPlayView(
            isAvailable,
            contentPosition,
            tape,
            playList,
            status,
            displayPlaying,
            onAudioItemClick,
            onChangeTapeSettings
        )
    }
}

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
                contentDescription = stringResource(R.string.audio_icon),
                modifier = Modifier.fillMaxSize()
            )
        }

        AudioPlayCurrentItem(
            isAvailable,
            tape,
            playList,
            status,
            metadata,
            Modifier.fillMaxWidth(),
            onAudioItemClick
        )

        PlaySliderItem(
            displayPlaying != DisplayPlayingSource.Pause,
            enabled = isAvailable && ItemStatus.isSeekable(status),
            contentPosition = if (contentPosition >= 0) contentPosition else tape.position,
            durationMs = metadata?.duration ?: 0,
            modifier = Modifier.fillMaxWidth(),
            amplitude = tape.volume,
            speed = tape.speed,
        ) {
            onAudioItemClick(AudioCallbackArgument.SeekTo(it))
        }

        AudioPlayPerformanceItem(
            isAvailable,
            status,
            displayPlaying,
            modifier = Modifier.fillMaxWidth(),
            onAudioItemClick
        )

        AudioPlaySettingsItem(tape, Modifier.fillMaxWidth(), onChangeTapeSettings)
    }
}

@Composable
fun AudioPlayViewPortrait(
    isAvailable: Boolean,
    contentPosition: Long,
    tape: AudioTapeDto,
    playList: List<AudioItemDto>,
    status: ItemStatus,
    displayPlaying: DisplayPlayingSource,
    onAudioItemClick: (AudioCallbackArgument) -> Unit = {},
    onChangeTapeSettings: (TapeSettingsCallbackArgument) -> Unit = {}
) {
    Column {
        Row(modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.AudioFile,
                    contentDescription = stringResource(R.string.audio_icon),
                    modifier = Modifier.fillMaxSize()
                )
            }
            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                AudioPlayPerformanceItem(
                    isAvailable,
                    status,
                    displayPlaying,
                    onAudioItemClick = onAudioItemClick
                )

                AudioPlaySettingsItem(
                    tape,
                    onChangeTapeSettings = onChangeTapeSettings
                )
            }
        }
        val metadata = playList.find { it.name == tape.currentName }?.metadata
        AudioPlayCurrentItem(
            isAvailable,
            tape,
            playList,
            status,
            metadata,
            onAudioItemClick = onAudioItemClick
        )

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
    }
}


@PreviewOrientation
@Composable
fun AudioPlayViewPreview() {
    AudioTapeTheme {
        AdaptiveAudioPlayView(
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