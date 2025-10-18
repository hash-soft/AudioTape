package com.hashsoft.audiotape.data

// Androidバージョンで分けなくていいようにする
data class VolumeItem(
    val name: String,
    val path: String,
    val lastModified: Long,
    val mediaStorageVolumeName: String
)


