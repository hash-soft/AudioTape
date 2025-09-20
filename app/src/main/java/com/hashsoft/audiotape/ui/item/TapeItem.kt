package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hashsoft.audiotape.data.AudioTapeSortOrder
import com.hashsoft.audiotape.logic.TimeFormat.Companion.formatMillis
import com.hashsoft.audiotape.ui.AudioCallbackArgument
import com.hashsoft.audiotape.ui.AudioCallbackResult


@Composable
fun TapeItem(
    index: Int,
    folderPath: String,
    currentName: String,
    position: Long,
    sortOrder: AudioTapeSortOrder,
    speed: Float,
    color: Int,
    audioCallback: (AudioCallbackArgument) -> AudioCallbackResult = { AudioCallbackResult.None }
) {
    ListItem(
        leadingContent = {
            Icon(
                imageVector = Icons.Default.LibraryMusic,
                null
            )
        },
        headlineContent = { Text(folderPath) },
        supportingContent = {
            Text(text = "current:${currentName}\nposition:${formatMillis(position)}\norder:${sortOrder} speed:${speed}")
        },
        modifier = Modifier.clickable {
            // Todo フォルダ遷移とかあるがひとまず押したら再生
            audioCallback(AudioCallbackArgument.TapeSelected(index))
        },
        colors = if (color > 0) ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant, // 背景色
            headlineColor = MaterialTheme.colorScheme.onSurfaceVariant         // 見出し文字色
        ) else {
            ListItemDefaults.colors()
        }
    )
}