package com.hashsoft.audiotape.data

import kotlinx.coroutines.flow.MutableStateFlow

class ControllerRepository {

    var isPlaying: MutableStateFlow<Boolean> = MutableStateFlow(false)
        private set

    var playbackPosition: MutableStateFlow<PlaybackPosition> =
        MutableStateFlow(PlaybackPosition.None)
        private set


    fun updateIsPlaying(value: Boolean) {
        isPlaying.value = value
    }

    fun updatePlaybackPosition(value: PlaybackPosition) {
        playbackPosition.value = value
    }

}
