package com.hashsoft.audiotape.data

sealed class PlaybackPosition {
    object None : PlaybackPosition()
    object Player : PlaybackPosition()
    data class Once(val position: Long) : PlaybackPosition()
}
