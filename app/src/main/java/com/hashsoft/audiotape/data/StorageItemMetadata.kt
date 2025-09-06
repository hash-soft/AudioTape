package com.hashsoft.audiotape.data

sealed interface StorageItemMetadata {
    data object Folder : StorageItemMetadata
    data object UnanalyzedFile : StorageItemMetadata
    data object InvalidFile : StorageItemMetadata
    data class Audio(
        val contents: AudioItemMetadata
    ) : StorageItemMetadata
}
