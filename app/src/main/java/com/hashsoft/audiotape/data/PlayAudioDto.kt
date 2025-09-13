package com.hashsoft.audiotape.data

data class PlayAudioDto(
    val isReadyOk: Boolean,
    val isPlaying: Boolean,
    val path: String,
    val durationMs: Long,
    val contentPosition: Long
)
