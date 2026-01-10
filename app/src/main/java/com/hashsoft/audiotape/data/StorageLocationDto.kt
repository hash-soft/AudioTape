package com.hashsoft.audiotape.data

enum class LocationType {
    Normal,
    Root,
    Inner,
    External
}

data class StorageLocationDto(
    val name: String,
    val path: String,
    val type: LocationType = LocationType.Normal
)
