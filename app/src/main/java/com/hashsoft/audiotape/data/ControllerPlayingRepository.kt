package com.hashsoft.audiotape.data

import kotlinx.coroutines.flow.MutableStateFlow

class ControllerPlayingRepository {

    var data: MutableStateFlow<Boolean> = MutableStateFlow(false)
        private set

    fun update(value: Boolean) {
        data.value = value
    }

}
