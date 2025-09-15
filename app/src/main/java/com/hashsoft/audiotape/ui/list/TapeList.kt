package com.hashsoft.audiotape.ui.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.ui.AudioCallbackArgument
import com.hashsoft.audiotape.ui.AudioCallbackResult
import com.hashsoft.audiotape.ui.item.TapeItem

@Composable
fun TapeList(
    audioTapeList: List<AudioTapeDto>,
    audioCallback: (AudioCallbackArgument) -> AudioCallbackResult = { AudioCallbackResult.None },
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(audioTapeList.size) {
            val item = audioTapeList[it]
            TapeItem(it, item, audioCallback)
        }
    }
}
