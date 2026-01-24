package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.data.AudioItemMetadata
import com.hashsoft.audiotape.logic.TextHelper
import com.hashsoft.audiotape.ui.AudioCallbackArgument
import com.hashsoft.audiotape.ui.animation.EqualizerAnimation
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme
import com.hashsoft.audiotape.ui.theme.LeadingContentWidth
import com.hashsoft.audiotape.ui.theme.currentItemBackgroundColor
import com.hashsoft.audiotape.ui.theme.currentItemContentColor

@Composable
fun AudioItem(
    audioIndex: Int,
    name: String,
    size: Long,
    lastModified: Long,
    metadata: AudioItemMetadata,
    color: Int = 0,
    icon: Int = 0,
    isResume: Boolean = false,
    contentPosition: Long = 0,
    audioCallback: (AudioCallbackArgument) -> Unit = {}
) {
    ListItem(
        leadingContent = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(LeadingContentWidth),
                contentAlignment = Alignment.Center
            ) {
                if (icon > 0) {
                    EqualizerAnimation()
                } else {
                    Text(
                        text = (audioIndex + 1).toString(),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        },
        overlineContent = { OverlineContext(metadata) },
        headlineContent = { Text(name) },
        supportingContent = {
            AudioFileSubInfoItem(size, lastModified, metadata.duration, isResume, contentPosition)
        },
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .combinedClickable(onClick = {
                audioCallback(
                    AudioCallbackArgument.AudioSelected(
                        index = audioIndex,
                        name = name,
                        position = contentPosition
                    )
                )
            }, onLongClick = {
                audioCallback(
                    AudioCallbackArgument.AudioSelected(
                        index = audioIndex,
                        name = name,
                        position = contentPosition,
                        transfer = true
                    )
                )
            }),
        colors = if (color > 0) ListItemDefaults.colors(
            containerColor = currentItemBackgroundColor, // 背景色
            headlineColor = currentItemContentColor,
            leadingIconColor = currentItemContentColor,
            overlineColor = currentItemContentColor,
            supportingColor = currentItemContentColor
        ) else {
            ListItemDefaults.colors()
        }
    )
}

@Composable
private fun OverlineContext(metadata: AudioItemMetadata) {

    val text = TextHelper.joinNonEmpty(
        stringResource(R.string.metadata_separator),
        metadata.artist,
        metadata.title,
        metadata.album
    )
    if (text.isNotEmpty()) {
        Text(text)
    }
}


@Preview(showBackground = true)
@Composable
fun AudioItemPreview() {
    AudioTapeTheme {
        AudioItem(
            98,
            "audio 00",
            6000,
            12300,
            AudioItemMetadata("album name", "title name", "artist", 0, 0),
            isResume = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AudioItemPreview2() {
    AudioTapeTheme {
        AudioItem(
            0,
            "audio 00",
            6000,
            12300,
            AudioItemMetadata("", "", "", 0, 0),
            icon = 1,
            isResume = true
        )
    }
}