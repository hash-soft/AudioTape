package com.hashsoft.audiotape.ui.dialog

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.ui.item.SimpleAudioItem
import com.hashsoft.audiotape.ui.theme.AudioListItemNumberPadding
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme
import com.hashsoft.audiotape.ui.theme.DialogCornerRadius

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
fun AudioPopupSelector(
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
            if (expanded) {
                AudioPopupSelectorDialog(
                    audioItemList,
                    targetIndex,
                    onDismissRequest = {
                        onExpandedChange(false)
                    }, onExpandedChange = onExpandedChange,
                    onItemClick = onItemClick
                )
            }

        }

    }
}


@Composable
private fun AudioPopupSelectorDialog(
    audioItemList: List<AudioItemDto> = emptyList(),
    selectedIndex: Int,
    onDismissRequest: () -> Unit,
    cornerRadius: Dp = DialogCornerRadius,
    onExpandedChange: (Boolean) -> Unit,
    onItemClick: (Int, Boolean) -> Unit = { _, _ -> }
) {
    val listState = rememberLazyListState()

    LaunchedEffect(selectedIndex) {
        // スクロールありの場合、選択項目を中央に表示する
        val viewportHeight = listState.layoutInfo.viewportSize.height
        val itemHeight = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 0
        val offset = (viewportHeight / 2) - (itemHeight / 2)
        listState.scrollToItem(index = selectedIndex, scrollOffset = -offset)
    }

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                state = listState
            ) {
                itemsIndexed(audioItemList) { index, item ->
                    val isTarget = index == selectedIndex
                    val rowTextColor =
                        if (isTarget) MaterialTheme.colorScheme.primary else LocalContentColor.current
                    CompositionLocalProvider(LocalContentColor provides rowTextColor) {
                        Row(modifier = Modifier.clickable {
                            onItemClick(index, isTarget)
                            onExpandedChange(false)
                        }) {
                            Text(
                                text = (index + 1).toString(),
                                modifier = Modifier
                                    .padding(AudioListItemNumberPadding)
                                    .align(Alignment.CenterVertically)
                            )
                            SimpleAudioItem(
                                item.name,
                                item.size,
                                item.lastModified,
                                item.metadata.duration
                            )
                        }
                    }
                    if (index < audioItemList.lastIndex) {
                        HorizontalDivider(
                            Modifier,
                            DividerDefaults.Thickness,
                            DividerDefaults.color
                        )
                    }


                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(stringResource(R.string.cancel))
            }
        },
        title = { Text(stringResource(R.string.audio_dialog_title)) },
        shape = RoundedCornerShape(cornerRadius)
    )
}


@Preview(showBackground = true)
@Composable
fun AudioPopupSelectorPreview() {
    AudioTapeTheme {
        AudioPopupSelector(
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