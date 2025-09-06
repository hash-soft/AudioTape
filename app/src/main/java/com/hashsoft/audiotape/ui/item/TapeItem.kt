package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.logic.TimeFormat.Companion.formatMillis


@Composable
fun TapeItem(item: AudioTapeDto, onTapeClick: () -> Unit = {}) {
    ListItem(
        leadingContent = {
            Icon(
                imageVector = Icons.Default.LibraryMusic,
                null
            )
        },
        headlineContent = { Text(item.folderPath) },
        supportingContent = {
            Text(text = "current:${item.currentName}\nposition:${formatMillis(item.position)}\norder:${item.sortOrder} speed:${item.speed}")
        },
        modifier = Modifier.clickable {
            onTapeClick()
        },
    )
}