package com.hashsoft.audiotape.data

import kotlinx.coroutines.flow.MutableStateFlow

// サービスからの操作を反映するためのRepository
class PlaybackRepository {

    var data: MutableStateFlow<PlaybackDto> = MutableStateFlow(PlaybackDto(false, false, "", 0))
        private set(value) {
            field = value
        }

    fun updateAll(isPlaying: Boolean, currentMediaId: String, contentPosition: Long){
        data.value = PlaybackDto(isPlaying, false, currentMediaId, contentPosition)
    }

    fun updatePlaying(isPlaying: Boolean) {
        data.value = data.value.copy(isPlaying = isPlaying)
    }

    fun updatePlayingPosition(isPlaying: Boolean, position: Long) {
        data.value = data.value.copy(isPlaying = isPlaying, contentPosition = position)
    }

    fun updatePlayingCurrentMediaId(isPlaying: Boolean, mediaId: String) {
        data.value = data.value.copy(isPlaying = isPlaying, currentMediaId = mediaId)
    }

    fun updateReadyOk(isReadyOk: Boolean) {
        data.value = data.value.copy(isReadyOk = isReadyOk)
    }

    fun updateCurrentMediaId(mediaId: String) {
        data.value = data.value.copy(currentMediaId = mediaId)
    }

    fun updateCurrentMediaIdPosition(mediaId: String, position: Long) {
        data.value = data.value.copy(currentMediaId = mediaId, contentPosition = position)
    }

    fun updateContentPosition(position: Long) {
        data.value = data.value.copy(contentPosition = position)
    }
}
