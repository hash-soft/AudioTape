package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.data.DisplayPlayingSource
import com.hashsoft.audiotape.data.ItemStatus
import com.hashsoft.audiotape.ui.AudioCallbackArgument
import com.hashsoft.audiotape.ui.button.PlayPauseButton
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme
import com.hashsoft.audiotape.ui.theme.IconMedium


@Composable
fun AudioPlayPerformanceItem(
    isAvailable: Boolean,
    status: ItemStatus,
    displayPlaying: DisplayPlayingSource,
    modifier: Modifier = Modifier,
    onAudioItemClick: (AudioCallbackArgument) -> Unit = {},
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onAudioItemClick(AudioCallbackArgument.SkipPrevious) },
            enabled = isAvailable && ItemStatus.isSkippable(status)
        ) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = stringResource(R.string.skip_previous),
            )
        }
        IconButton(
            onClick = { onAudioItemClick(AudioCallbackArgument.BackIncrement) },
            enabled = isAvailable && ItemStatus.isSeekable(status)
        ) {
            Icon(
                imageVector = Icons.Default.FastRewind,
                contentDescription = stringResource(R.string.fast_rewind),
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
                contentDescription = stringResource(R.string.fast_forward),
            )
        }
        IconButton(
            onClick = { onAudioItemClick(AudioCallbackArgument.SkipNext) },
            enabled = isAvailable && ItemStatus.isSkippable(status)
        ) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = stringResource(R.string.skip_next),
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AudioPlayPerformanceItemPreview() {
    AudioTapeTheme {
        AudioPlayPerformanceItem(
            isAvailable = true,
            status = ItemStatus.Normal,
            displayPlaying = DisplayPlayingSource.Pause
        )
    }
}