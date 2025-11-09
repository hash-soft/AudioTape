package com.hashsoft.audiotape.ui.item

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview


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
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun LabelItemPreview() {
    LabelItem(label = "For you")
}
