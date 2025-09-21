package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.hashsoft.audiotape.ui.AudioCallbackArgument
import com.hashsoft.audiotape.ui.AudioCallbackResult
import com.hashsoft.audiotape.ui.bar.AudioSeekbar

@Composable
fun AudioPlayItem(
    path: String,
    isReadyOk: Boolean = true,
    isPlaying: Boolean = false,
    durationMs: Long = 0,
    contentPosition: Long = 0,
    audioCallback: (AudioCallbackArgument) -> AudioCallbackResult = { AudioCallbackResult.None }
) {
    ListItem(
        leadingContent = {
            Box {
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
            }
        },
        overlineContent = {
            Row {
                Text(
                    path,
                    maxLines = 1,
                    overflow = TextOverflow.StartEllipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { audioCallback(AudioCallbackArgument.SkipPrevious) },
                    enabled = isReadyOk
                ) { Icon(Icons.Default.SkipPrevious, null) }
                IconButton(
                    onClick = { audioCallback(AudioCallbackArgument.SkipNext) },
                    enabled = isReadyOk
                ) {
                    Icon(Icons.Default.SkipNext, null)
                }
            }
        },
        headlineContent = {},
        supportingContent = {
            AudioSeekbar(
                position = contentPosition,
                durationMs = durationMs,
                isPlaying = isPlaying,
                enabled = isReadyOk,
                audioCallback = audioCallback
            )
        },
        trailingContent = null,
        modifier = Modifier.clickable {
            // Todo 再生専用画面へ
            //audioCallback(AudioCallbackArgument.Selected)
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant, // 背景色
        )
    )
}
