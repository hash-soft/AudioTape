package com.hashsoft.audiotape.ui.dropdown

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.ui.item.SimpleAudioItem

/**
 * オーディオ項目のドロップダウンメニュー
 *
 * @param expanded ドロップダウンメニューが開いているかどうか
 * @param onExpandedChange ドロップダウンメニューの開閉状態が変更されたときのコールバック
 * @param trigger ドロップダウンメニューのトリガーとなるコンポーザブル
 * @param audioItemList オーディオ項目のリスト
 * @param targetName 対象のオーディオ項目の名前
 * @param onItemClick オーディオ項目がクリックされたときのコールバック
 */
@Composable
fun AudioDropDown(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    trigger: @Composable (() -> Unit),
    audioItemList: List<AudioItemDto> = emptyList(),
    targetName: String = "",
    onItemClick: (Int, Boolean) -> Unit = { index, lastCurrent -> }
) {
    Box(
        modifier = Modifier
            .background(Color.Red)
    ) {
        trigger()

        DropdownMenu(expanded = expanded, onDismissRequest = {
            onExpandedChange(false)
        }) {
            audioItemList.forEachIndexed { index, item ->
                val isTarget = item.name == targetName
                DropdownMenuItem(
                    text = {
                        SimpleAudioItem(
                            item.name,
                            item.size,
                            item.lastModified,
                            item.metadata.duration
                        )
                    },
                    onClick = {
                        onItemClick(index, isTarget)
                        onExpandedChange(false)
                    },
                    leadingIcon = { Text(text = (index + 1).toString()) },
                    colors = if (isTarget) MenuDefaults.itemColors(
                        textColor = MaterialTheme.colorScheme.primary,
                        leadingIconColor = MaterialTheme.colorScheme.primary
                    ) else {
                        MenuDefaults.itemColors()
                    }
                )
                if (index < audioItemList.lastIndex) {
                    HorizontalDivider(
                        Modifier,
                        DividerDefaults.Thickness,
                        DividerDefaults.color
                    )
                }

            }
        }

    }
}
