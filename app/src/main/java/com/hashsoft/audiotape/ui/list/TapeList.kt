package com.hashsoft.audiotape.ui.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.ui.item.TapeItem

@Composable
fun TapeList(audioTapeList: List<AudioTapeDto>) {
    //val uiState by controllerViewModel.uiState.collectAsStateWithLifecycle()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(audioTapeList.size) {
            val item = audioTapeList[it]
            TapeItem(item)
        }
    }
}
