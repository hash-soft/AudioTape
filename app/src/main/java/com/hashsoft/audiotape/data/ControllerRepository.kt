package com.hashsoft.audiotape.data

import kotlinx.coroutines.flow.MutableStateFlow

class ControllerRepository {

    var playbackStatus: MutableStateFlow<PlaybackStatus> = MutableStateFlow(PlaybackStatus())
        private set


    var playbackPosition: MutableStateFlow<PlaybackPosition> =
        MutableStateFlow(PlaybackPosition.None)
        private set

    fun updatePlaybackIsPlaying(isPlaying: Boolean) {
        playbackStatus.value = playbackStatus.value.copy(isPlaying = isPlaying)
    }

    fun updatePlaybackPlayWhenReady(playWhenReady: Boolean) {
        playbackStatus.value = playbackStatus.value.copy(playWhenReady = playWhenReady)
    }

    fun updatePlaybackPlayerState(state: Int) {
        playbackStatus.value = playbackStatus.value.copy(playerState = state)
    }

    fun updatePlaybackPosition(value: PlaybackPosition) {
        playbackPosition.value = value
    }

}
