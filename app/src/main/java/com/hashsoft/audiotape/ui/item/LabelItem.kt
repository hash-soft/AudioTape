package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hashsoft.audiotape.ui.theme.SettingLabelHorizontalPadding
import com.hashsoft.audiotape.ui.theme.SettingLabelVerticalPadding


/**
 * ラベルアイテム
 *
 * @param label ラベル
 * @param modifier モディファイア
 */
@Composable
fun LabelItem(label: String, modifier: Modifier = Modifier) {
    Text(
        text = label,
        fontSize = MaterialTheme.typography.labelLarge.fontSize,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(
            horizontal = SettingLabelHorizontalPadding,
            vertical = SettingLabelVerticalPadding
        )
    )
}

@Preview(showBackground = true)
@Composable
fun LabelItemPreview() {
    LabelItem(label = "For you")
}
