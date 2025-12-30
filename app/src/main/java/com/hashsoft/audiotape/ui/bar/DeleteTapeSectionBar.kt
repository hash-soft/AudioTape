package com.hashsoft.audiotape.ui.bar


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme
import com.hashsoft.audiotape.ui.theme.SelectionBarElevation


@Composable
fun DeleteTapeSelectionBar(
    count: Int,
    total: Int,
    onClose: () -> Unit = {},
    onSelectAll: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Surface(
        tonalElevation = SelectionBarElevation,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(R.string.delete_tape_close_description)
                )
            }
            Text(
                text = stringResource(R.string.delete_tape_select_label, count, total),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onSelectAll) {
                Icon(
                    Icons.Default.SelectAll,
                    contentDescription = stringResource(R.string.delete_tape_select_all_description)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_tape_select_delete_description)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DeleteTapeSelectionBarPreview() {
    AudioTapeTheme {
        DeleteTapeSelectionBar(3, 10)
    }
}