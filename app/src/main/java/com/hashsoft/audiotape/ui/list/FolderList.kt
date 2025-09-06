package com.hashsoft.audiotape.ui.list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hashsoft.audiotape.data.DisplayStorageItemExtra
import com.hashsoft.audiotape.data.StorageItemDto
import com.hashsoft.audiotape.data.StorageItemMetadata
import com.hashsoft.audiotape.ui.AudioCallbackArgument
import com.hashsoft.audiotape.ui.AudioCallbackResult
import com.hashsoft.audiotape.ui.item.AudioItem
import com.hashsoft.audiotape.ui.item.FolderItem
import com.hashsoft.audiotape.ui.item.InvalidFileItem
import com.hashsoft.audiotape.ui.item.UnanalyzedFileItem

@Composable
fun FolderList(
    modifier: Modifier = Modifier,
    storageItemList: List<StorageItemDto> = emptyList(),
    storageItemExtra: DisplayStorageItemExtra = DisplayStorageItemExtra(-1, false, 0),
    audioCallback: (AudioCallbackArgument) -> AudioCallbackResult = { AudioCallbackResult.None }
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(storageItemList.size) {
            val item = storageItemList[it]
            when (item.metadata) {
                is StorageItemMetadata.Audio -> {
                    if (it == storageItemExtra.index) {
                        AudioItem(
                            index = it,
                            item.name,
                            item.size,
                            item.lastModified,
                            item.metadata.contents,
                            isPlaying = storageItemExtra.isPlaying,
                            isCurrent = true,
                            contentPosition = storageItemExtra.positionMs,
                            audioCallback = audioCallback
                        )
                    } else {
                        AudioItem(
                            index = it,
                            item.name,
                            item.size,
                            item.lastModified,
                            item.metadata.contents,
                            audioCallback = audioCallback
                        )
                    }

                }

                is StorageItemMetadata.UnanalyzedFile -> {
                    UnanalyzedFileItem(
                        item.name,
                        item.size,
                        item.lastModified,
                        index = it,
                        audioCallback = audioCallback
                    )
                }

                is StorageItemMetadata.InvalidFile -> {
                    InvalidFileItem(
                        item.name,
                        item.size,
                        item.lastModified,
                        index = it
                    )
                }

                is StorageItemMetadata.Folder -> FolderItem(
                    item.path,
                    item.name,
                    item.lastModified,
                    audioCallback
                )
            }
        }
    }
}