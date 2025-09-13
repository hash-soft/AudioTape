package com.hashsoft.audiotape.data

import kotlinx.coroutines.flow.MutableStateFlow

// Resume時の再生状態を保持するRepository
class ResumeAudioRepository {

    var data: MutableStateFlow<ResumeAudioDto> =
        MutableStateFlow(ResumeAudioDto("", -1, -1))
        private set(value) {
            field = value
        }

    fun updateAll(
        currentName: String,
        durationMs: Long,
        contentPosition: Long,
        sortOrder: AudioTapeSortOrder
    ) {
        data.value = ResumeAudioDto(currentName, durationMs, contentPosition, sortOrder)
    }
}
