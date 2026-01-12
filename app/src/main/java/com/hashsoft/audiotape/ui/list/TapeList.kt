package com.hashsoft.audiotape.ui.list

import androidx.collection.IntSet
import androidx.collection.intSetOf
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.logic.StorageHelper
import com.hashsoft.audiotape.ui.AudioCallbackArgument
import com.hashsoft.audiotape.ui.DisplayTapeItem
import com.hashsoft.audiotape.ui.bar.DeleteTapeSelectionBar
import com.hashsoft.audiotape.ui.item.TapeCheckItem
import com.hashsoft.audiotape.ui.item.TapeItem

@Composable
fun TapeList(
    displayTapeList: List<DisplayTapeItem>,
    deleteMode: Boolean = false,
    deleteIdsSet: IntSet = intSetOf(),
    onCloseSelected: () -> Unit = {},
    onCheckedChange: (checked: Boolean, index: Int) -> Unit,
    onSelectedAllCheck: () -> Unit,
    onTapeDelete: () -> Unit,
    audioCallback: (AudioCallbackArgument) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (deleteMode) {
            stickyHeader {
                DeleteTapeSelectionBar(
                    deleteIdsSet.size,
                    displayTapeList.size,
                    onClose = onCloseSelected,
                    onSelectAll = onSelectedAllCheck,
                    onDelete = onTapeDelete
                )
            }
        }
        items(displayTapeList.size) {
            val item = displayTapeList[it]
            val tape = item.audioTape
            val title = StorageHelper.treeListToString(
                item.treeList,
                stringResource(R.string.path_separator),
                default = item.audioTape.folderPath
            )
            if (deleteMode) {
                TapeCheckItem(
                    index = it,
                    title = title,
                    tape.folderPath,
                    currentNo = item.currentAudioNo,
                    item.audioList.size,
                    tape.currentName,
                    tape.position,
                    tape.sortOrder,
                    repeat = tape.repeat,
                    speed = tape.speed,
                    volume = tape.volume,
                    pitch = tape.pitch,
                    tape.createTime,
                    tape.lastPlayedAt,
                    item.isCurrent,
                    item.status,
                    isChecked = deleteIdsSet.contains(it),
                    onCheckedChange = onCheckedChange
                )
            } else {
                TapeItem(
                    index = it,
                    title = title,
                    tape.folderPath,
                    currentNo = item.currentAudioNo,
                    item.audioList.size,
                    tape.currentName,
                    tape.position,
                    tape.sortOrder,
                    repeat = tape.repeat,
                    speed = tape.speed,
                    volume = tape.volume,
                    pitch = tape.pitch,
                    tape.createTime,
                    tape.lastPlayedAt,
                    item.isCurrent,
                    item.status,
                    audioCallback = audioCallback
                )
            }
        }
    }
}
