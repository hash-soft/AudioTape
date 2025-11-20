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

    fun updateReadyStateToOff() {
        if (data.value.isReadyOk) {
            data.value = data.value.copy(isReadyOk = false)
        }
    }

}
