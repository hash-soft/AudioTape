package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hashsoft.audiotape.ui.theme.SettingItemHorizontalPadding
import com.hashsoft.audiotape.ui.theme.SettingItemVerticalPadding

/**
 * クリック可能な設定項目
 *
 * @param title タイトル
 * @param value 値
 * @param onClick クリック時のコールバック
 * @param modifier Modifier
 */
@Composable
fun ClickableSettingItem(
    title: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = SettingItemHorizontalPadding,
                vertical = SettingItemVerticalPadding
            )
    ) {
        Text(
            text = title,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = value,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ClickableSettingItemPreview() {
    ClickableSettingItem(title = "タイトル", value = "サマリー", onClick = {})
}