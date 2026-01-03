package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.logic.TimeFormat
import com.hashsoft.audiotape.ui.AudioCallbackArgument
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme


@Composable
fun FolderItem(
    path: String,
    name: String,
    audioCount: Int,
    lastModified: Long,
    audioCallback: (AudioCallbackArgument) -> Unit = {}
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
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.audio_items_label, audioCount),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (lastModified > 0) TimeFormat.formatDateTimeHm(
                        lastModified
                    ) else stringResource(R.string.no_last_modified_label),
                )
            }

        },
        modifier = Modifier.clickable {
            audioCallback(AudioCallbackArgument.FolderSelected(path))
        },
    )
}


@Preview(showBackground = true)
@Composable
fun FolderItemPreview() {
    AudioTapeTheme {
        FolderItem(
            "",
            "name",
            10,
            100000,
        )
    }
}