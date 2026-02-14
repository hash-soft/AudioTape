package com.hashsoft.audiotape.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme


@Composable
fun PrivacyPolicyDialog(onDismissResult: () -> Unit = {}) {
    AlertDialog(
        onDismissRequest = { onDismissResult() },
        confirmButton = {
            TextButton(onClick = { onDismissResult() }) { Text(stringResource(R.string.privacy_policy_close_label)) }
        },
        title = { Text(stringResource(R.string.privacy_policy_title)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(R.string.privacy_policy_content),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun PrivacyPolicyDialogPreview() {
    AudioTapeTheme {
        PrivacyPolicyDialog()
    }
}
