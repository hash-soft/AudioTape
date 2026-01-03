package com.hashsoft.audiotape.ui.list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.data.FolderItemDto
import com.hashsoft.audiotape.data.StorageItem
import com.hashsoft.audiotape.ui.AudioCallbackArgument
import com.hashsoft.audiotape.ui.item.AudioItem
import com.hashsoft.audiotape.ui.item.FolderItem

/**
 * フォルダリスト
 *
 * @param modifier Modifier
 * @param storageItemList ストレージアイテムリスト
 * @param audioCallback オーディオコールバック
 */
@Composable
fun FolderList(
    modifier: Modifier = Modifier,
    storageItemList: List<StorageItem> = listOf(),
    expandIndexList: List<Int> = listOf(),
    isPlaying: Boolean = false,
    isCurrent: Boolean = false,
    targetName: String = "",
    contentPosition: Long = 0,
    audioCallback: (AudioCallbackArgument) -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(storageItemList.size) {
            when (val item = storageItemList[it]) {
                is AudioItemDto -> {
                    val isTarget = item.name == targetName
                    val isResume = !isCurrent && isTarget
                    AudioItem(
                        index = it,
                        audioIndex = expandIndexList.getOrElse(it) { 0 },
                        item.name,
                        item.size,
                        item.lastModified,
                        item.metadata,
                        when {
                            isResume -> 2
                            isTarget -> 1
                            else -> 0
                        },
                        if (isCurrent && isTarget && isPlaying) 1 else 0,
                        isResume = isResume,
                        if (isTarget) contentPosition else 0,
                        audioCallback = audioCallback
                    )
                }

                is FolderItemDto -> FolderItem(
                    item.absolutePath,
                    item.name,
                    item.itemCount,
                    item.lastModified,
                    audioCallback
                )
            }
        }
    }
}
