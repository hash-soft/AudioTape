package com.hashsoft.audiotape.data

data class ResumeAudioDto(
    val path: String,
    val durationMs: Long,
    val contentPosition: Long,
    val sortOrder: AudioTapeSortOrder = AudioTapeSortOrder.NAME_ASC,
)
