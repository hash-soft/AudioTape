package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview

/**
 * スイッチアイテム
 *
 * @param title タイトル
 * @param check チェック状態
 * @param onCheckedChange チェック状態変更コールバック
 * @param modifier モディファイア
 */
@Composable
fun SwitchItem(
    title: String,
    check: Boolean,
    onCheckedChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isChecked = remember(check) { mutableStateOf(check) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .toggleable(
                value = isChecked.value,
                onValueChange = {
                    isChecked.value = it
                    onCheckedChange(it)
                },
                role = Role.Switch
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge
        )

        Switch(
            checked = isChecked.value,
            onCheckedChange = {
                isChecked.value = it
                onCheckedChange(it)
            },
        )
    }
}


@Preview(showBackground = true)
@Composable
fun SwitchItemPreview() {
    SwitchItem(title = "タイトル", check = true)
}
