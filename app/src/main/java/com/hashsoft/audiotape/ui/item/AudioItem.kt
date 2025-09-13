package com.hashsoft.audiotape.ui.item

import android.graphics.BitmapFactory
import android.text.format.Formatter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.hashsoft.audiotape.data.AudioItemMetadata
import com.hashsoft.audiotape.logic.TimeFormat
import com.hashsoft.audiotape.ui.AudioCallbackArgument
import com.hashsoft.audiotape.ui.AudioCallbackResult

@Composable
fun AudioItem(
    index: Int,
    name: String,
    size: Long,
    lastModified: Long,
    metadata: AudioItemMetadata,
    isPlaying: Boolean = false,
    isCurrent: Boolean = false,
    contentPosition: Long = 0,
    audioCallback: (AudioCallbackArgument) -> AudioCallbackResult = { AudioCallbackResult.None }
) {
    val context = LocalContext.current
    ListItem(
        leadingContent = {
            // プレイ中は特殊なものにしたい
            Icon(
                imageVector = if (isPlaying) Icons.Default.PlayArrow else Icons.Default.AudioFile,
                null
            )
        },
        overlineContent = { OverlineContext(metadata) },
        headlineContent = { Text(name) },
        supportingContent = {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    TimeFormat.formatMillis(metadata.duration),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${
                        Formatter.formatFileSize(
                            context,
                            size
                        )
                    } ${
                        TimeFormat.formatDateTime(
                            lastModified
                        )
                    }"
                )
            }
        },
        trailingContent =
            if (metadata.artwork.isNotEmpty()) {
                {
                    val picture = metadata.artwork.toByteArray()
                    Icon(
                        bitmap = BitmapFactory.decodeByteArray(
                            picture,
                            0,
                            picture.size
                        ).asImageBitmap(), null
                    )
                }
            } else {
                null
            },
        modifier = Modifier.clickable {
            audioCallback(
                AudioCallbackArgument.AudioSelected(
                    index = index,
                    name,
                    metadata,
                    isPlaying,
                    isCurrent,
                    contentPosition
                )
            )
        },
        colors = if (isCurrent) ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant, // 背景色
            headlineColor = MaterialTheme.colorScheme.onSurfaceVariant         // 見出し文字色
        ) else {
            ListItemDefaults.colors()
        }
    )
}

@Composable
private fun OverlineContext(metadata: AudioItemMetadata) {
    val text = buildString {
        var sep = false
        if (metadata.artist.isNotEmpty()) {
            append(metadata.artist)
            sep = true
        }
        if (metadata.title.isNotEmpty()) {
            if (sep) {
                append(" - ")
            }
            append(metadata.title)
            sep = true
        }
        if (metadata.album.isNotEmpty()) {
            if (sep) {
                append(" - ")
            }
            append(metadata.album)
        }
    }
    if (text.isNotEmpty()) {
        Text(text)
    }
}