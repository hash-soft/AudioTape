package com.hashsoft.audiotape.logic

data class StorageItem(
    val name: String,
    val path: String,
    val size: Long,
    val lastModified: Long,
    val isDirectory: Boolean
)