package com.hashsoft.audiotape.data

sealed class PlaybackPosition {
    object None : PlaybackPosition()
    data class Player(val position: Long, val skipInitial: Boolean = false) : PlaybackPosition()
    data class Once(val position: Long) : PlaybackPosition()
}
