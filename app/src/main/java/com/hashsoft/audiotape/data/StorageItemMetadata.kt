package com.hashsoft.audiotape.data

sealed interface StorageItemMetadata {
    data object Folder : StorageItemMetadata
    data object InvalidFile : StorageItemMetadata
    data class Audio(
        val contents: AudioItemMetadata
    ) : StorageItemMetadata
}
