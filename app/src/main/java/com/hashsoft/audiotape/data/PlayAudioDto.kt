package com.hashsoft.audiotape.data

data class PlayAudioDto(
    val exist: Boolean,
    val isReadyOk: Boolean,
    val isPlaying: Boolean,
    val path: String,
    val durationMs: Long,
    val contentPosition: Long,
    val audioTape: AudioTapeDto
)
