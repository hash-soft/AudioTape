package com.hashsoft.audiotape.data

data class AudioItemMetadata(
    val album: String,
    val title: String,
    val artist: String,
    val duration: Long,
    val artwork: List<Byte> = emptyList()
)
