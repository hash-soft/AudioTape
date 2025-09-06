package com.hashsoft.audiotape.data

enum class LocationType {
    Normal,
    Root,
    Home
}

data class StorageLocationDto(
    val name: String,
    val path: String,
    val type: LocationType = LocationType.Normal
)
