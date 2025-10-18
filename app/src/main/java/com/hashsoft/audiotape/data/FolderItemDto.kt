package com.hashsoft.audiotape.data


data class FolderItemDto(
    override val name: String,
    override val absolutePath: String,
    override val relativePath: String,
    override val lastModified: Long,
    val itemCount: Long
) : StorageItem
