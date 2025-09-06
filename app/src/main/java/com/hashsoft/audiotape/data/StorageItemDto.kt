package com.hashsoft.audiotape.data


data class StorageItemDto(
    val name: String,
    val path: String,
    val size: Long,
    val lastModified: Long,
    val metadata: StorageItemMetadata = StorageItemMetadata.Folder
)
