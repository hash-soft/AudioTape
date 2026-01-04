package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.hashsoft.audiotape.data.DisplayPlayingSource
import com.hashsoft.audiotape.ui.AudioCallbackArgument
import com.hashsoft.audiotape.ui.text.AudioDurationText
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme
import com.hashsoft.audiotape.ui.theme.ListLabelSpace
import com.hashsoft.audiotape.ui.theme.NoGap
import com.hashsoft.audiotape.ui.theme.SimpleAudioPlayBorder
import com.hashsoft.audiotape.ui.theme.SimpleAudioPlayItemEndPadding
import com.hashsoft.audiotape.ui.theme.simpleAudioPlayBackgroundColor
import com.hashsoft.audiotape.ui.theme.simpleAudioPlayBorderColor
import com.hashsoft.audiotape.ui.theme.simpleAudioPlayContentColor
import com.hashsoft.audiotape.ui.theme.simpleAudioPlayIndicatorColor
import com.hashsoft.audiotape.ui.theme.simpleAudioPlayIndicatorTrackColor

@Composable
fun SimpleAudioPlayItemPortrait(
    directory: String,
    name: String,
    isAvailable: Boolean = false,
    displayPlaying: DisplayPlayingSource,
    durationMs: Long = 0,
    contentPosition: Long = 0,
    enableTransfer: Boolean = true,
    audioCallback: (AudioCallbackArgument) -> Unit
) {
    Surface(
        contentColor = simpleAudioPlayContentColor,
        modifier = Modifier
            .background(color = simpleAudioPlayBorderColor)
            .padding(top = SimpleAudioPlayBorder)
    ) {
        val baseModifier =
            if (enableTransfer) Modifier.clickable { audioCallback(AudioCallbackArgument.TransferAudioPlay) } else Modifier
        Row(
            modifier = baseModifier
                .background(color = simpleAudioPlayBackgroundColor)
                .padding(bottom = SimpleAudioPlayItemEndPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = {
                audioCallback(AudioCallbackArgument.PlayPause(displayPlaying != DisplayPlayingSource.Pause))
            }, enabled = isAvailable) {
                Icon(
                    imageVector = if (displayPlaying != DisplayPlayingSource.Pause) Icons.Default.Pause else Icons.Default.PlayArrow,
                    null
                )

            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ListLabelSpace),
                ) {
                    Text(
                        directory,
                        modifier = Modifier
                            .weight(1f)
                            .alignByBaseline(),
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        maxLines = 1,
                        overflow = TextOverflow.StartEllipsis
                    )
                    AudioDurationText(
                        duration = contentPosition,
                        modifier = Modifier.alignByBaseline(),
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                }
                Text(
                    text = name,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                LinearProgressIndicator(
                    progress = { contentPosition.toFloat() / durationMs },
                    color = simpleAudioPlayIndicatorColor,
                    trackColor = simpleAudioPlayIndicatorTrackColor,
                    modifier = Modifier.fillMaxWidth(),
                    strokeCap = StrokeCap.Butt, // 淵を平らにする
                    gapSize = NoGap,    // 進捗の境目の点
                    drawStopIndicator = {}  // Endの印
                )
            }
            IconButton(
                onClick = { audioCallback(AudioCallbackArgument.SkipPrevious) },
                enabled = isAvailable,
            ) { Icon(Icons.Default.SkipPrevious, null) }
            IconButton(
                onClick = { audioCallback(AudioCallbackArgument.SkipNext) },
                enabled = isAvailable,
            ) {
                Icon(Icons.Default.SkipNext, null)
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun SimpleAudioPlayItemPortraitPreview() {
    AudioTapeTheme {
        SimpleAudioPlayItemPortrait(
            directory = "テープ名000000000000000000000000000000",
            name = "name",
            isAvailable = true,
            displayPlaying = DisplayPlayingSource.Pause,
            durationMs = 1000,
            contentPosition = 500,
            audioCallback = {}
        )
    }
}