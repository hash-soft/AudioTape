package com.hashsoft.audiotape.data

import kotlinx.coroutines.flow.MutableStateFlow

// サービスからの操作を反映するためのRepository
class PlaybackRepository {

    var data: MutableStateFlow<PlaybackDto> =
        MutableStateFlow(
            PlaybackDto(
                isReadyOk = false,
                isPlaying = false,
                currentName = "",
                folderPath = ",",
                durationMs = -1,
                contentPosition = -1
            )
        )
        private set

    fun updateAll(
        isReady: Boolean,
        isPlaying: Boolean,
        currentName: String,
        folderPath: String,
        durationMs: Long,
        contentPosition: Long
    ) {
        data.value =
            PlaybackDto(isReady, isPlaying, currentName, folderPath, durationMs, contentPosition)
    }

    fun updateWithoutStringItem(
        isReadyOk: Boolean,
        isPlaying: Boolean,
        durationMs: Long,
        contentPosition: Long
    ) {
        data.value = data.value.copy(
            isReadyOk = isReadyOk,
            isPlaying = isPlaying,
            durationMs = durationMs,
            contentPosition = contentPosition
        )
    }


    fun updateContentPosition(position: Long) {
        data.value = data.value.copy(contentPosition = position)
    }
}
