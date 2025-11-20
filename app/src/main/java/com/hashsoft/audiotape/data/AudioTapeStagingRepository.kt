package com.hashsoft.audiotape.data

import kotlinx.coroutines.flow.MutableStateFlow

class AudioTapeStagingRepository {

    var data: MutableStateFlow<AudioTapeStaging> =
        MutableStateFlow(
            AudioTapeStaging(
                currentName = "",
                folderPath = ",",
                position = 0
            )
        )
        private set

    fun updateAll(
        folderPath: String,
        currentName: String,
        position: Long
    ) {
        data.value = AudioTapeStaging(folderPath = folderPath, currentName = currentName, position)
    }

    fun updatePosition(position: Long) {
        data.value = data.value.copy(position = position)
    }

}
