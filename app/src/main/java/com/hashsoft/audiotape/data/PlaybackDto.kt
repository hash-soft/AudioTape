package com.hashsoft.audiotape.data

data class PlaybackDto(
    val isReadyOk: Boolean,
    val isPlaying: Boolean,
    val currentMediaId: String,
    val contentPosition: Long
)
