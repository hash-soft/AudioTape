package com.hashsoft.audiotape.ui.dialog

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme


@Composable
fun HelpDialog(onDismissResult: () -> Unit = {}) {
    AlertDialog(
        onDismissRequest = { onDismissResult() },
        confirmButton = {
            TextButton(onClick = { onDismissResult() }) {
                Text(stringResource(R.string.ok))
            }
        },
        title = { Text("アプリの操作方法") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HelpItem("📁 フォルダ選択", "音楽や音声ファイルが入ったフォルダを選択します。")
                HelpItem("📼 再生・一時停止", "カセット部分をタップして再生/停止を切り替えます。")
                HelpItem("💾 自動保存", "再生位置はフォルダごとに自動で記録されます。")
                HelpItem("⏪ 巻き戻し/早送り", "カセット特有の操作感で、前後へスキップできます。")
            }
        }
    )
}

@Composable
private fun HelpItem(title: String, description: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(text = description, style = MaterialTheme.typography.bodyMedium)
    }
}


@Preview(showBackground = true)
@Composable
fun HelpDialogPreview() {
    AudioTapeTheme {
        HelpDialog()
    }
}