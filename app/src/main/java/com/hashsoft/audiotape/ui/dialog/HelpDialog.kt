package com.hashsoft.audiotape.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme
import com.hashsoft.audiotape.ui.theme.HelpIconSize
import com.hashsoft.audiotape.ui.theme.HelpItemSpace
import com.hashsoft.audiotape.ui.theme.HelpVerticalSpace


@Composable
fun HelpDialog(onDismissResult: () -> Unit = {}) {
    AlertDialog(
        onDismissRequest = { onDismissResult() },
        confirmButton = {
            TextButton(onClick = { onDismissResult() }) {
                Text(stringResource(R.string.ok))
            }
        },
        title = { Text(stringResource(R.string.help_title)) },
        text = {
            val helpItems = listOf(
                HelpItem(
                    Icons.Default.Folder,
                    R.string.help_select_folder_title,
                    R.string.help_select_folder_description
                ),
                HelpItem(
                    Icons.Default.LibraryMusic,
                    R.string.help_select_tape_title,
                    R.string.help_select_tape_description
                ),
                HelpItem(
                    Icons.Default.PlayCircleOutline,
                    R.string.help_mini_player_title,
                    R.string.help_mini_player_description
                ),
                HelpItem(
                    Icons.Default.AudioFile,
                    R.string.help_player_screen_title,
                    R.string.help_player_screen_description
                )
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(HelpVerticalSpace)
            ) {
                items(helpItems) { item ->
                    HelpRow(item)
                }
            }
        }
    )
}

@Composable
private fun HelpRow(item: HelpItem) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            modifier = Modifier.size(HelpIconSize),
        )
        Spacer(modifier = Modifier.width(HelpItemSpace))
        Column {
            Text(
                text = stringResource(item.titleRes),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(item.descRes),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HelpDialogPreview() {
    AudioTapeTheme {
        HelpDialog()
    }
}