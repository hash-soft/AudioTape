package com.hashsoft.audiotape

import kotlinx.serialization.Serializable

object Route {
    @Serializable
    data object Library

    @Serializable
    data object AudioPlay

    @Serializable
    data object UserSettings
}