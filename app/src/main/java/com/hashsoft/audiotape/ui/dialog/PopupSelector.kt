package com.hashsoft.audiotape.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.ui.theme.DialogCornerRadius

/**
 * 設定項目を選択するダイアログ
 *
 * @param title ダイアログのタイトル
 * @param options 選択肢のリスト
 * @param selectedIndex 選択されている項目のインデックス
 * @param onSelect 項目が選択されたときのコールバック
 * @param onDismissRequest ダイアログが閉じられたときのコールバック
 * @param cornerRadius ダイアログの角の半径
 */
@Composable
fun SelectSettingDialog(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    cancelButton: Boolean = false,
    onSelect: (index: Int) -> Unit,
    onDismissRequest: () -> Unit,
    cornerRadius: Dp = DialogCornerRadius
) {
    val listState = rememberLazyListState()

    LaunchedEffect(selectedIndex) {
        // スクロールありの場合、選択項目を中央に表示する
        val viewportHeight = listState.layoutInfo.viewportSize.height
        val itemHeight = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 0
        val offset = (viewportHeight / 2) - (itemHeight / 2)
        listState.scrollToItem(index = selectedIndex, scrollOffset = -offset)
    }

    if (cancelButton) {
        SelectSettingDialogWithCancelButton(
            title = title,
            options = options,
            selectedIndex = selectedIndex,
            onSelect = onSelect,
            onDismissRequest = onDismissRequest,
            cornerRadius = cornerRadius,
            listState = listState
        )
    } else {
        SelectSettingDialogNormal(
            title = title,
            options = options,
            selectedIndex = selectedIndex,
            onSelect = onSelect,
            onDismissRequest = onDismissRequest, cornerRadius = cornerRadius,
            listState = listState
        )
    }

}


@Composable
private fun SelectSettingDialogNormal(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (index: Int) -> Unit,
    onDismissRequest: () -> Unit,
    cornerRadius: Dp = DialogCornerRadius,
    listState: LazyListState
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(cornerRadius),
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = cornerRadius,
                    vertical = cornerRadius
                )
            ) {
                Text(
                    text = title,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                )

                LazyColumn(modifier = Modifier.fillMaxWidth(), state = listState) {
                    itemsIndexed(options) { index, label ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Max)
                                .clickable { onSelect(index) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (index == selectedIndex),
                                onClick = { onSelect(index) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            )

                            Text(
                                text = label,
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun SelectSettingDialogWithCancelButton(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (index: Int) -> Unit,
    onDismissRequest: () -> Unit,
    cornerRadius: Dp = DialogCornerRadius,
    listState: LazyListState
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text(text = title) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                state = listState
            ) {
                itemsIndexed(options) { index, label ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max)
                            .clickable { onSelect(index) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (index == selectedIndex),
                            onClick = { onSelect(index) }
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton =
            {
                TextButton(onClick = { onDismissRequest() }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        shape = RoundedCornerShape(cornerRadius)
    )
}


@Preview(showBackground = true)
@Composable
fun SelectSettingDialogPreview() {
    SelectSettingDialog(
        title = "dummy",
        options = listOf("極小", "小", "中", "大", "極大"),
        selectedIndex = 0,
        cancelButton = true,
        onSelect = {},
        onDismissRequest = {})
}
