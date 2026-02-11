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
import androidx.compose.ui.tooling.preview.Preview
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme


@Composable
fun PrivacyPolicyDialog(onDismissResult: () -> Unit = {}) {
    AlertDialog(
        onDismissRequest = { onDismissResult() },
        confirmButton = {
            TextButton(onClick = { onDismissResult() }) { Text("同意して閉じる") }
        },
        title = { Text("プライバシーポリシー") },
        text = {
            // 長文に対応するため、ここでも verticalScroll を使用
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = """
                            【個人情報の収集について】
                            本アプリは、ユーザーの個人情報を収集・送信することはありません。
                            
                            【ストレージへのアクセス】
                            音声ファイルの再生および管理のため、端末内の特定フォルダへのアクセス許可を使用しますが、これらのデータが外部に送信されることはありません。
                            
                            【免責事項】
                            本アプリの利用により生じた損害について、開発者は一切の責任を負いません。
                            
                            ...（以下、必要な文章を追記）...
                        """.trimIndent(),
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
