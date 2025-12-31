package com.hashsoft.audiotape.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hashsoft.audiotape.R


@Composable
fun DeleteTapeConfirmDialog(onConfirmResult: () -> Unit, onDismissResult: () -> Unit) {
    AlertDialog(
        title = { Text(text = stringResource(R.string.delete_confirm_title)) },
        text = { Text(text = stringResource(R.string.delete_confirm_text)) },
        onDismissRequest = {},
        dismissButton = {
            TextButton(onClick = onDismissResult) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirmResult) {
                Text(text = stringResource(R.string.ok))
            }
        }
    )
}
