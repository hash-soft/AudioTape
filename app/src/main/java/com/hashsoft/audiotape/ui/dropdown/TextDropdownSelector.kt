package com.hashsoft.audiotape.ui.dropdown

import androidx.collection.IntSet
import androidx.collection.intSetOf
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * テキストドロップダウンセレクター
 *
 * @param labels ラベルのリスト
 * @param title タイトル
 * @param iconContent アイコンコンテンツ
 * @param onItemSelected アイテム選択時のコールバック
 */
@Composable
fun TextDropdownSelector(
    labels: List<String>,
    title: String = "",
    selectedIndex: Int = -1,
    enabled: Boolean = true,
    disableMenuIds: IntSet = intSetOf(),
    iconContent: @Composable (() -> Unit),
    onItemSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }, content = iconContent, enabled = enabled)

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (title.isNotEmpty()) {
                Text(
                    text = title,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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
