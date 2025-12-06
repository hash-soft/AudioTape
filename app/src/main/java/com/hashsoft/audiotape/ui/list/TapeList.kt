package com.hashsoft.audiotape.ui.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hashsoft.audiotape.data.DisplayAudioTape
import com.hashsoft.audiotape.ui.AudioCallbackArgument
import com.hashsoft.audiotape.ui.item.TapeItem

@Composable
fun TapeList(
    audioTapeList: List<DisplayAudioTape>,
    audioCallback: (AudioCallbackArgument) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(audioTapeList.size) {
            val item = audioTapeList[it]
            val tape = item.base
            TapeItem(
                index = it,
                tape.folderPath,
                tape.currentName,
                tape.position,
                tape.sortOrder,
                repeat = tape.repeat,
                speed = tape.speed,
                volume = tape.volume,
                pitch = tape.pitch,
                tape.createTime,
                tape.updateTime,
                color = item.color,
                audioCallback = audioCallback
            )
        }
    }
}
