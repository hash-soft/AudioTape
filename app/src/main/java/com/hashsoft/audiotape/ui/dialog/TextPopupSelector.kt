package com.hashsoft.audiotape.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme
import com.hashsoft.audiotape.ui.theme.DisabledAlpha
import com.hashsoft.audiotape.ui.theme.TextDropdownPadding

/**
 * テキストポップアップセレクター
 *
 * @param labels ラベルのリスト
 * @param title タイトル
 * @param buttonContent アイコンコンテンツ
 * @param onItemSelected アイテム選択時のコールバック
 */
@Composable
fun TextPopupSelector(
    labels: List<String>,
    title: String = "",
    selectedIndex: Int = -1,
    enabled: Boolean = true,
    nonIconContent: Boolean = false,
    buttonContent: @Composable (() -> Unit),
    onItemSelected: (Int) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = if (nonIconContent) Modifier.wrapContentSize(Alignment.TopStart) else Modifier) {
        if (nonIconContent) {
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(CircleShape)
                    .clickable(enabled = enabled, onClick = { expanded = true })
                    .alpha(if (enabled) 1f else DisabledAlpha)
                    .padding(TextDropdownPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                buttonContent()
            }
        } else {
            IconButton(onClick = { expanded = true }, content = buttonContent, enabled = enabled)
        }

        if (expanded) {
            SelectSettingDialog(
                title = title,
                options = labels,
                selectedIndex = selectedIndex,
                cancelButton = true,
                onSelect = {
                    expanded = false
                    if (it == selectedIndex) return@SelectSettingDialog
                    onItemSelected(it)
                },
                onDismissRequest = { expanded = false }
            )

        }
    }
}


@Preview(showBackground = true)
@Composable
fun TextPopupSelectorPreview() {
    AudioTapeTheme {
        TextPopupSelector(
            labels = listOf("label1", "label2"),
            title = "title",
            selectedIndex = 0,
            enabled = false,
            buttonContent = { Text("label1") }
        )
    }
}