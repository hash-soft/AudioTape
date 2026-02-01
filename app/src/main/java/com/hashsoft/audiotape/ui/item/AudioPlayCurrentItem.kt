package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.data.AudioItemMetadata
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.ItemStatus
import com.hashsoft.audiotape.logic.TextHelper
import com.hashsoft.audiotape.ui.AudioCallbackArgument
import com.hashsoft.audiotape.ui.dropdown.AudioDropDown
import com.hashsoft.audiotape.ui.text.TappableMarqueeText
import com.hashsoft.audiotape.ui.theme.AudioPlayItemHorizonalPadding
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme
import com.hashsoft.audiotape.ui.theme.audioPlayFileAlpha


@Composable
fun AudioPlayCurrentItem(
    isAvailable: Boolean,
    tape: AudioTapeDto,
    playList: List<AudioItemDto>,
    status: ItemStatus,
    metadata: AudioItemMetadata?,
    modifier: Modifier = Modifier,
    onAudioItemClick: (AudioCallbackArgument) -> Unit = {},
) {
    Row(
        modifier = modifier
            .padding(horizontal = AudioPlayItemHorizonalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            TappableMarqueeText(
                text = if (metadata == null) "" else TextHelper.joinNonEmpty(
                    stringResource(R.string.metadata_separator),
                    metadata.artist,
                    metadata.title,
                    metadata.album
                ),
                color = LocalContentColor.current.copy(alpha = audioPlayFileAlpha(status)),
                style = MaterialTheme.typography.bodyMedium,
            )
            TappableMarqueeText(
                text = tape.currentName,
                color = LocalContentColor.current.copy(alpha = audioPlayFileAlpha(status)),
                style = MaterialTheme.typography.titleLarge
            )
        }
        AudioListDropdownSelector(
            playList,
            tape.currentName,
            enabled = isAvailable && ItemStatus.isPlayable(status),
            onItemSelected = onAudioItemClick
        )
    }
}


/**
 * オーディオリストドロップダウンセレクター
 *
 * @param playList 再生リスト
 * @param onItemSelected アイテム選択時のコールバック
 */
@Composable
private fun AudioListDropdownSelector(
    playList: List<AudioItemDto>,
    targetName: String = "",
    enabled: Boolean = true,
    onItemSelected: (AudioCallbackArgument) -> Unit = {}
) {
    val expanded = remember { mutableStateOf(false) }

    AudioDropDown(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = it },
        trigger = {
            IconButton(onClick = { expanded.value = true }, enabled = enabled) {
                Icon(
                    Icons.AutoMirrored.Filled.ListAlt,
                    contentDescription = stringResource(R.string.list_description)
                )
            }
        },
        audioItemList = playList,
        targetName = targetName
    ) { index, lastCurrent ->
        if (!lastCurrent) {
            val audioItem = playList.getOrNull(index) ?: return@AudioDropDown
            onItemSelected(AudioCallbackArgument.AudioSelected(index, audioItem.name, 0))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AudioPlayCurrentItemPreview() {
    AudioTapeTheme {
        AudioPlayCurrentItem(
            isAvailable = true,
            tape = AudioTapeDto("folderPath", "currentPath", "currentName"),
            playList = listOf(
                AudioItemDto(
                    "currentName",
                    "",
                    "",
                    0,
                    0,
                    0,
                    "",
                    AudioItemMetadata("album", "title", "artist", 30000, 0)
                )
            ),
            status = ItemStatus.Normal,
            metadata = AudioItemMetadata("album", "title", "artist", 30000, 0),
        )
    }
}