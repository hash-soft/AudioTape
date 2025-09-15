package com.hashsoft.audiotape.ui.item

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.hashsoft.audiotape.ui.AudioCallbackArgument
import com.hashsoft.audiotape.ui.AudioCallbackResult

@Composable
fun InvalidFileItem(
    name: String,
    size: Long,
    lastModified: Long,
    index: Int,
    audioCallback: (AudioCallbackArgument) -> AudioCallbackResult = { AudioCallbackResult.None }
) {
    // Todo 押したら無効ファイルを示すトーストを出すかも
    ListItem(
        leadingContent = {
            // プレイ中は特殊なものにしたい
            Icon(
                imageVector = Icons.Default.Error,
                null
            )
        },
        headlineContent = { Text(name) },
        supportingContent = {
            FileSubInfoItem(size, lastModified)
        },

        colors = ListItemDefaults.colors()
    )
}
