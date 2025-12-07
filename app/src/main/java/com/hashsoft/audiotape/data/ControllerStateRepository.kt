package com.hashsoft.audiotape.data

import androidx.media3.common.Player
import kotlinx.coroutines.flow.MutableStateFlow

class ControllerStateRepository {

    var data: MutableStateFlow<ControllerState> =
        MutableStateFlow(
            ControllerState(
                playbackState = Player.STATE_IDLE,
                isPlaying = false,
            )
        )
        private set

    fun updateAll(playbackState: Int, isPlaying: Boolean) {
        data.value = ControllerState(playbackState, isPlaying)
    }

    fun updatePlaybackState(playbackState: Int) {
        data.value = data.value.copy(playbackState = playbackState)
    }

    fun updateIsPlaying(isPlaying: Boolean) {
        data.value = data.value.copy(isPlaying = isPlaying)
    }


}
