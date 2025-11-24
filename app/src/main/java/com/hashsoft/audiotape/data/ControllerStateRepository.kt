package com.hashsoft.audiotape.data

import kotlinx.coroutines.flow.MutableStateFlow

class ControllerStateRepository {

    var data: MutableStateFlow<ControllerState> =
        MutableStateFlow(
            ControllerState(
                isReadyOk = false,
                isPlaying = false,
            )
        )
        private set

    fun updateAll(isReady: Boolean, isPlaying: Boolean) {
        data.value = ControllerState(isReady, isPlaying)
    }

    fun updateIsReadyOk(isReadyOk: Boolean) {
        data.value = data.value.copy(isReadyOk = isReadyOk)
    }

    fun updateIsPlaying(isPlaying: Boolean) {
        data.value = data.value.copy(isPlaying = isPlaying)
    }


}
