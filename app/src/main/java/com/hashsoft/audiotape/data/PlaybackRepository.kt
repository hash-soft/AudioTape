package com.hashsoft.audiotape.data

import kotlinx.coroutines.flow.MutableStateFlow

// サービスからの操作を反映するためのRepository
class PlaybackRepository {

    var data: MutableStateFlow<PlaybackDto> =
        MutableStateFlow(PlaybackDto(false, false, "", -1, -1))
        private set(value) {
            field = value
        }

    fun updateAll(
        isReady: Boolean,
        isPlaying: Boolean,
        currentName: String,
        durationMs: Long,
        contentPosition: Long
    ) {
        data.value = PlaybackDto(isReady, isPlaying, currentName, durationMs, contentPosition)
    }

    fun updateWithoutCurrentName(
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

    fun updatePlaying(isPlaying: Boolean) {
        data.value = data.value.copy(isPlaying = isPlaying)
    }

    fun updateReadyOk(isReadyOk: Boolean) {
        data.value = data.value.copy(isReadyOk = isReadyOk)
    }

    fun updatePlayingPosition(isPlaying: Boolean, position: Long) {
        data.value = data.value.copy(isPlaying = isPlaying, contentPosition = position)
    }

    fun updateContentPosition(position: Long) {
        data.value = data.value.copy(contentPosition = position)
    }
}
