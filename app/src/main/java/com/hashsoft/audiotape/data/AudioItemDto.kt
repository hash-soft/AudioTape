package com.hashsoft.audiotape.data


data class AudioItemDto(
    val name: String,
    val path: String,
    val size: Long,
    val lastModified: Long,
    val metadata: AudioItemMetadata
)
