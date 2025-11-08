package com.hashsoft.audiotape.ui.dialog

import android.os.Build
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.hashsoft.audiotape.R

/**
 * パーミッションが必要な理由を説明するダイアログ
 *
 * @param onDialogResult ダイアログの結果を通知するコールバック
 */
@Composable
fun PermissionRationaleDialog(onDialogResult: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        title = { Text(context.getString(R.string.permission_required)) },
        text = { Text(text = context.getString(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) R.string.permission_rationale_33 else R.string.permission_rationale_legacy)) },
        onDismissRequest = {},
        confirmButton = {
            TextButton(onClick = onDialogResult) {
                Text(text = context.getString(R.string.ok))
            }
        }
    )
}
