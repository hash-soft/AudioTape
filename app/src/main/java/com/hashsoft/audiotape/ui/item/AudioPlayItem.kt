package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.hashsoft.audiotape.ui.AudioCallbackArgument
import com.hashsoft.audiotape.ui.AudioCallbackResult
import com.hashsoft.audiotape.ui.bar.AudioSeekbar

@Composable
fun AudioPlayItem(
    path: String,
    isCurrent: Boolean = false,
    isPlaying: Boolean = false,
    durationMs: Long = 0,
    contentPosition: Long = 0,
    enabled: Boolean = true,
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

                Icon(
                    imageVector = if (isCurrent) Icons.Default.FolderOpen else Icons.Default.Folder,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.BottomEnd),
                )
            }
        },
        overlineContent = {
            Text(path, maxLines = 1, overflow = TextOverflow.StartEllipsis)
        },
        headlineContent = {
            AudioSeekbar(
                position = contentPosition,
                durationMs = durationMs,
                isPlaying = isPlaying,
                enabled = enabled,
                audioCallback = audioCallback
            )
        },
        supportingContent = null,
        trailingContent = null,
        modifier = Modifier.clickable {
            //audioCallback(AudioCallbackArgument.Selected)
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant, // 背景色
        )
    )
}
