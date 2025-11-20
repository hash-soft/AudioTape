package com.hashsoft.audiotape.ui

import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.ControllerState
import com.hashsoft.audiotape.data.DisplayAudioTape
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TapeListState(
    private val _list: MutableStateFlow<List<DisplayAudioTape>> = MutableStateFlow(
        emptyList()
    ),
) {
    val list: StateFlow<List<DisplayAudioTape>> = _list.asStateFlow()

    fun updateList(
        audioTapeList: List<AudioTapeDto>,
        controllerState: ControllerState,
        playingFolderPath: String
    ) {
        val list = audioTapeList.map {
            makeDisplayAudioTape(it, controllerState.isPlaying, playingFolderPath)
        }
        _list.update { list }
    }

    private fun makeDisplayAudioTape(
        item: AudioTapeDto,
        isPlaying: Boolean,
        playingFolderPath: String
    ): DisplayAudioTape {
        val color = if (item.folderPath != playingFolderPath) {
            0
        } else {
            // Todo 無効なパスやファイルかどうかも判断する
            if (isPlaying) 2 else 1
        }
        return DisplayAudioTape(item, color)
    }


}



