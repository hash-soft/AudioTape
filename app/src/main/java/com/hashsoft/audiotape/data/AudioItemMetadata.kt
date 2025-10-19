package com.hashsoft.audiotape.data

data class AudioItemMetadata(
    val album: String,
    val title: String,
    val artist: String,
    val duration: Long,
    val albumId: Long,
    val artwork: List<Byte> = emptyList()
)
