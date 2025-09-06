package com.hashsoft.audiotape.ui.dropdown

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.ui.AudioCallbackResult
import com.hashsoft.audiotape.ui.item.AudioItem

@Composable
fun AudioDropDown(initialSelected: String, audioItemList: List<AudioItemDto> = emptyList<AudioItemDto>()) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(initialSelected) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Red)
            .clickable { expanded = !expanded }
            .wrapContentSize()
    ) {

        Text(
            text = selectedOptionText,
            //  textAlign = TextAlign.Center,
        )

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            for (item in audioItemList) {
                AudioItem(
                    index = 0,
                    item.name,
                    item.size,
                    item.lastModified,
                    item.metadata,
                ) {
                    selectedOptionText = item.name
                    expanded = false
                    return@AudioItem AudioCallbackResult.None
                }
            }
        }

    }
}