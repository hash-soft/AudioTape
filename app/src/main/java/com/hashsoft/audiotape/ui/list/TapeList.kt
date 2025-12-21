package com.hashsoft.audiotape.ui.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.logic.StorageHelper
import com.hashsoft.audiotape.ui.AudioCallbackArgument
import com.hashsoft.audiotape.ui.DisplayTapeItem
import com.hashsoft.audiotape.ui.item.TapeItem

@Composable
fun TapeList(
    displayTapeList: List<DisplayTapeItem>,
    audioCallback: (AudioCallbackArgument) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(displayTapeList.size) {
            val item = displayTapeList[it]
            val tape = item.audioTape
            TapeItem(
                index = it,
                title = StorageHelper.treeListToString(
                    item.treeList,
                    stringResource(R.string.path_separator),
                    default = item.audioTape.folderPath
                ),
                tape.folderPath,
                tape.currentName,
                tape.position,
                tape.sortOrder,
                repeat = tape.repeat,
                speed = tape.speed,
                volume = tape.volume,
                pitch = tape.pitch,
                tape.createTime,
                tape.lastPlayedAt,
                color = if (item.isCurrent) 1 else 0,
                audioCallback = audioCallback
            )
        }
    }
}
