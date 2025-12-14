package com.hashsoft.audiotape.data

import kotlinx.coroutines.flow.MutableStateFlow

class ControllerRepository {

    var isPlaying: MutableStateFlow<Boolean> = MutableStateFlow(false)
        private set

    var playbackPositionSource: MutableStateFlow<PlaybackPositionSource> =
        MutableStateFlow(PlaybackPositionSource.None)
        private set

    fun updateIsPlaying(value: Boolean) {
        isPlaying.value = value
    }

    fun updatePlaybackPositionSource(value: PlaybackPositionSource) {
        playbackPositionSource.value = value
    }

}
