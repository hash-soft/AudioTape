package com.hashsoft.audiotape.data


sealed interface StorageItem {
    val name: String
    val absolutePath: String
    val relativePath: String
    val lastModified: Long
}


