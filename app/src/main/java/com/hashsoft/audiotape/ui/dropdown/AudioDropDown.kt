package com.hashsoft.audiotape.ui.dropdown

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.ui.item.SimpleAudioItem
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme

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
    onItemClick: (Int, Boolean) -> Unit = { _, _ -> }
) {
    Box {
        trigger()

        val itemHeightPx = with(LocalDensity.current) { 48.dp.toPx() }
        val targetIndex = audioItemList.indexOfFirst { it.name == targetName }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                onExpandedChange(false)
            },
            scrollState = ScrollState((targetIndex * itemHeightPx).toInt()),
        ) {
            audioItemList.forEachIndexed { index, item ->
                val isTarget = index == targetIndex
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


@Preview(showBackground = true)
@Composable
fun AudioDropDownPreview() {
    AudioTapeTheme {
        AudioDropDown(
            expanded = false,
            onExpandedChange = { },
            trigger = {
                IconButton(onClick = { }, enabled = true) {
                    Icon(
                        Icons.AutoMirrored.Filled.ListAlt,
                        contentDescription = stringResource(R.string.list_description)
                    )
                }
            },
            audioItemList = listOf(),
            targetName = "name"
        )

    }
}