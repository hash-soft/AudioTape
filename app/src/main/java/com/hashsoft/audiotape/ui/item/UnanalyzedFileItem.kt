package com.hashsoft.audiotape.ui.item

import android.text.format.Formatter
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.hashsoft.audiotape.logic.TimeFormat
import com.hashsoft.audiotape.ui.AudioCallbackArgument
import com.hashsoft.audiotape.ui.AudioCallbackResult

@Composable
fun UnanalyzedFileItem(
    name: String,
    size: Long,
    lastModified: Long,
    index: Int,
    audioCallback: (AudioCallbackArgument) -> AudioCallbackResult = { AudioCallbackResult.None }
) {
    LaunchedEffect(Unit) {
        audioCallback(AudioCallbackArgument.Display(index))
    }

    val context = LocalContext.current
    ListItem(
        leadingContent = {
            // プレイ中は特殊なものにしたい
            Icon(
                imageVector = Icons.Default.AudioFile,
                null
            )
        },
        overlineContent = {
            Text("")
        },
        headlineContent = { Text(name) },
        supportingContent = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${
                        Formatter.formatFileSize(
                            context,
                            size
                        )
                    } ${
                        TimeFormat.formatDateTime(
                            lastModified
                        )
                    }",
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        },

        colors = ListItemDefaults.colors()
    )
}
