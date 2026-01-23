package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.hashsoft.audiotape.ui.theme.SettingItemHorizontalPadding
import com.hashsoft.audiotape.ui.theme.SettingItemVerticalPadding

/**
 * スイッチアイテム
 *
 * @param title タイトル
 * @param check チェック状態
 * @param modifier モディファイア
 * @param onCheckedChange チェック状態変更コールバック
 */
@Composable
fun SwitchItem(
    title: String,
    check: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit = {}
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
            )
            .padding(
                horizontal = SettingItemHorizontalPadding,
                vertical = SettingItemVerticalPadding
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier
                .weight(1f),
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
