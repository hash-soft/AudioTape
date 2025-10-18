package com.hashsoft.audiotape.data

data class DisplayStorageItem(
    val base: StorageItem,
    val index: Int,
    val color: Int = 0,
    val icon: Int = 0,
    val isResume: Boolean = false,
    val contentPosition: Long = 0,
)
