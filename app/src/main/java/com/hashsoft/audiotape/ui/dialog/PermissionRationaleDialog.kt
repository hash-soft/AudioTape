package com.hashsoft.audiotape.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.hashsoft.audiotape.R

@Composable
fun PermissionRationaleDialog(onDialogResult: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        title = { Text(context.getString(R.string.permission_required)) },
        // Todo 個々の文言をAndroidバージョンの権限名に合わせて変える
        text = { Text(context.getString(R.string.permission_rationale)) },
        onDismissRequest = {},
        confirmButton = {
            TextButton(onClick = onDialogResult) {
                Text("OK")
            }
        }
    )
}