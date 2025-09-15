package com.hashsoft.audiotape.data

data class PlaybackDto(
    val isReadyOk: Boolean,
    val isPlaying: Boolean,
    val currentName: String,
    val folderPath: String,
    val durationMs: Long,
    val contentPosition: Long
)
