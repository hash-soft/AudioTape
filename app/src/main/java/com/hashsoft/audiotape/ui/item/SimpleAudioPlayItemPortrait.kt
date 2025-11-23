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
import com.hashsoft.audiotape.ui.AudioCallbackArgument
import com.hashsoft.audiotape.ui.AudioCallbackResult

@Composable
fun SimpleAudioPlayItemPortrait(
    directory: String,
    name: String,
    isReadyOk: Boolean = false,
    isPlaying: Boolean = false,
    durationMs: Long = 0,
    contentPosition: Long = 0,
    audioCallback: (AudioCallbackArgument) -> AudioCallbackResult = { AudioCallbackResult.None }
) {
    Row(
        Modifier
            .clickable { audioCallback(AudioCallbackArgument.TransferAudioPlay) }
            .background(MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = {
            audioCallback(
                AudioCallbackArgument.PlayPause(
                    isPlaying,
                )
            )
        }) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                null
            )

        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                directory,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                maxLines = 1,
                overflow = TextOverflow.StartEllipsis,
            )
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
            enabled = isReadyOk,
        ) { Icon(Icons.Default.SkipPrevious, null) }
        IconButton(
            onClick = { audioCallback(AudioCallbackArgument.SkipNext) },
            enabled = isReadyOk,
        ) {
            Icon(Icons.Default.SkipNext, null)
        }
    }

//    ListItem(
//        leadingContent = {
//            Box {
//                IconButton(onClick = {
//                    audioCallback(
//                        AudioCallbackArgument.PlayPause(
//                            isPlaying,
//                        )
//                    )
//                }) {
//                    Icon(
//                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
//                        null
//                    )
//                }
//            }
//        },
//        overlineContent = {
//            Row {
//                Text(
//                    path,
//                    maxLines = 1,
//                    overflow = TextOverflow.StartEllipsis,
//                    modifier = Modifier.weight(1f)
//                )
//            }
//        },
//        headlineContent = {},
//        supportingContent = {
//            LinearProgressIndicator(
//                progress = { contentPosition.toFloat() / durationMs },
//                modifier = Modifier
//                    .defaultMinSize(minWidth = 0.dp)
//                    .fillMaxWidth()
//                    .padding(start = 0.dp),
//                gapSize = 0.dp,
//                drawStopIndicator = {}
//            )
//        },
//        trailingContent = {
//            IconButton(
//                onClick = { audioCallback(AudioCallbackArgument.SkipPrevious) },
//                enabled = isReadyOk
//            ) { Icon(Icons.Default.SkipPrevious, null) }
//            IconButton(
//                onClick = { audioCallback(AudioCallbackArgument.SkipNext) },
//                enabled = isReadyOk
//            ) {
//                Icon(Icons.Default.SkipNext, null)
//            }
//        },
//        modifier = Modifier.clickable {
//            audioCallback(AudioCallbackArgument.TransferAudioPlay)
//        },
//        colors = ListItemDefaults.colors(
//            containerColor = MaterialTheme.colorScheme.surfaceVariant, // 背景色
//        )
//    )
}
