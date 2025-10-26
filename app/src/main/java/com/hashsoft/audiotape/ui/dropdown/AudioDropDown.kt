package com.hashsoft.audiotape.ui.dropdown

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.data.DisplayStorageItem
import com.hashsoft.audiotape.ui.item.SimpleAudioItem

@Composable
fun AudioDropDown(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    trigger: @Composable (() -> Unit),
    audioItemList: List<DisplayStorageItem<AudioItemDto>> = emptyList(),
    onItemClick: (Int) -> Unit = {}
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
                val base = item.base
                DropdownMenuItem(
                    text = {
                        SimpleAudioItem(
                            index,
                            base.name,
                            base.size,
                            base.lastModified,
                            base.metadata.duration,
                            item.color
                        )
                    },
                    onClick = { onItemClick(index) }
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