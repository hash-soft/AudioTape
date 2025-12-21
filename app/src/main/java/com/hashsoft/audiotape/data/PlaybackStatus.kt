package com.hashsoft.audiotape.data

import androidx.media3.common.Player

data class PlaybackStatus(
    val isPlaying: Boolean = false,
    val playWhenReady: Boolean = false,
    val playerState: Int = Player.STATE_IDLE
)

