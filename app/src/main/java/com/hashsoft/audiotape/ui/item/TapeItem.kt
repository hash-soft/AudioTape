package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.logic.TimeFormat.Companion.formatMillis
import com.hashsoft.audiotape.ui.AudioCallbackArgument
import com.hashsoft.audiotape.ui.AudioCallbackResult


@Composable
fun TapeItem(
    index: Int,
    item: AudioTapeDto, // Todo 後で項目をばらす
    audioCallback: (AudioCallbackArgument) -> AudioCallbackResult = { AudioCallbackResult.None }
) {
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
            // Todo フォルダ遷移とかあるがひとまず押したら再生
            audioCallback(AudioCallbackArgument.TapeSelected(index))
        },
    )
}