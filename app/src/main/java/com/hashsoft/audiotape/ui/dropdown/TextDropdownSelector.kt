package com.hashsoft.audiotape.ui.dropdown

import androidx.collection.IntSet
import androidx.collection.intSetOf
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme
import com.hashsoft.audiotape.ui.theme.DisabledAlpha
import com.hashsoft.audiotape.ui.theme.DropDownTitleHorizonalPadding
import com.hashsoft.audiotape.ui.theme.DropDownTitleVerticalPadding
import com.hashsoft.audiotape.ui.theme.TextDropdownPadding

/**
 * テキストドロップダウンセレクター
 *
 * @param labels ラベルのリスト
 * @param title タイトル
 * @param buttonContent アイコンコンテンツ
 * @param onItemSelected アイテム選択時のコールバック
 */
@Composable
fun TextDropdownSelector(
    labels: List<String>,
    title: String = "",
    selectedIndex: Int = -1,
    enabled: Boolean = true,
    disableMenuIds: IntSet = intSetOf(),
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

        val itemHeightPx = with(LocalDensity.current) { 48.dp.toPx() }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            scrollState = ScrollState(if (selectedIndex <= 0) 0 else (selectedIndex * itemHeightPx).toInt())
        ) {
            if (title.isNotEmpty()) {
                Text(
                    text = title,
                    modifier = Modifier.padding(
                        horizontal = DropDownTitleHorizonalPadding,
                        vertical = DropDownTitleVerticalPadding
                    ),
                    style = MaterialTheme.typography.labelLarge,
                )
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            }

            labels.forEachIndexed { index, label ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = label,
                            color = if (selectedIndex == index) MaterialTheme.colorScheme.primary else Color.Unspecified
                        )
                    },
                    onClick = {
                        onItemSelected(index)
                        expanded = false
                    },
                    enabled = disableMenuIds.contains(index).not()
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TextDropdownSelectorPreview() {
    AudioTapeTheme {
        TextDropdownSelector(
            labels = listOf("label1", "label2"),
            title = "title",
            selectedIndex = 0,
            enabled = false,
            buttonContent = { Text("label1") }
        )
    }
}