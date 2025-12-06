package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hashsoft.audiotape.logic.TimeFormat
import com.hashsoft.audiotape.ui.AudioCallbackArgument


@Composable
fun FolderItem(
    path: String,
    name: String,
    lastModified: Long,
    audioCallback: (AudioCallbackArgument) -> Unit
) {
    ListItem(
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Folder,
                null
            )
        },
        headlineContent = { Text(name) },
        supportingContent = {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Todo ---をリソースからとるようにする
                Text(
                    text = if (lastModified > 0) TimeFormat.formatDateTimeHm(
                        lastModified
                    ) else "---",
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

        },
        modifier = Modifier.clickable {
            audioCallback(AudioCallbackArgument.FolderSelected(path))
        },
    )
}