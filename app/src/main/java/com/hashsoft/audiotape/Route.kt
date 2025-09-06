package com.hashsoft.audiotape

import kotlinx.serialization.Serializable

object Route {
    @Serializable
    data object Library

    @Serializable
    data class AudioPlay(
        val folderPath: String,
        val currentName: String
    )

    @Serializable
    data object UserSettings
}