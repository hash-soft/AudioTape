package com.hashsoft.audiotape.ui.bar


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.ui.dropdown.TextDropdownSelector
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme
import com.hashsoft.audiotape.ui.theme.tapeBarBackgroundColor
import com.hashsoft.audiotape.ui.theme.tapeBarContentColor


@Composable
fun TapeBar(
    sortIndex: Int,
    onSortChange: (sortOrder: Int) -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Surface(
        contentColor = tapeBarContentColor,
        color = tapeBarBackgroundColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val sortLabels = stringArrayResource(R.array.tape_list_sort_labels).toList()

            TextDropdownSelector(
                sortLabels,
                "",
                selectedIndex = sortIndex,
                iconContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        contentDescription = stringResource(R.string.menu_description),
                    )
                }) {
                onSortChange(it)
            }

            IconButton(
                onClick = { onDeleteClick() },
                content = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        null,
                    )
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TapeBarPreview() {
    AudioTapeTheme {
        TapeBar(0, onSortChange = {}) {}
    }
}