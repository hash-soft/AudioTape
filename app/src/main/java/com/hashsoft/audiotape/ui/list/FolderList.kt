package com.hashsoft.audiotape.ui.list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.data.DisplayStorageItem
import com.hashsoft.audiotape.data.FolderItemDto
import com.hashsoft.audiotape.ui.AudioCallbackArgument
import com.hashsoft.audiotape.ui.AudioCallbackResult
import com.hashsoft.audiotape.ui.item.AudioItem
import com.hashsoft.audiotape.ui.item.FolderItem

@Composable
fun FolderList(
    modifier: Modifier = Modifier,
    storageItemList: List<DisplayStorageItem> = emptyList(),
    audioCallback: (AudioCallbackArgument) -> AudioCallbackResult = { AudioCallbackResult.None }
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(storageItemList.size) {
            val item = storageItemList[it]
            val base = item.base
            when (base) {
                is AudioItemDto -> {
                    AudioItem(
                        index = it,
                        base.name,
                        base.size,
                        base.lastModified,
                        base.metadata,
                        item.color,
                        item.icon,
                        item.isResume,
                        item.contentPosition,
                        audioCallback = audioCallback
                    )
                }

                is FolderItemDto -> FolderItem(
                    base.absolutePath,
                    base.name,
                    base.lastModified,
                    audioCallback
                )
            }
        }
    }
}