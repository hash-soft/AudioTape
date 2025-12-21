package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hashsoft.audiotape.data.DisplayPlayingSource
import com.hashsoft.audiotape.ui.AudioCallbackArgument
import com.hashsoft.audiotape.ui.text.AudioDurationText

@Composable
fun SimpleAudioPlayItemPortrait(
    directory: String,
    name: String,
    isAvailable: Boolean = false,
    displayPlaying: DisplayPlayingSource,
    durationMs: Long = 0,
    contentPosition: Long = 0,
    audioCallback: (AudioCallbackArgument) -> Unit
) {
    Row(
        Modifier
            .clickable { audioCallback(AudioCallbackArgument.TransferAudioPlay) }
            .background(MaterialTheme.colorScheme.surfaceVariant),
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    directory,
                    modifier = Modifier.weight(1f),
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.StartEllipsis
                )
                AudioDurationText(
                    duration = contentPosition,
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
                modifier = Modifier
                    .defaultMinSize(minWidth = 0.dp)
                    .fillMaxWidth()
                    .padding(start = 0.dp),
                gapSize = 0.dp,
                drawStopIndicator = {}
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
