package com.hashsoft.audiotape.logic

import androidx.media3.common.Player
import com.hashsoft.audiotape.data.DisplayPlayingSource
import com.hashsoft.audiotape.data.PlaybackStatus

class PlaybackHelper {
    companion object {

        fun playbackStatusToDisplayPlayingSource(status: PlaybackStatus): DisplayPlayingSource {
            return if (status.playerState == Player.STATE_BUFFERING) {
                // Readyになったら再生される場合だけBufferingにする
                if (status.playWhenReady) DisplayPlayingSource.Buffering else DisplayPlayingSource.Pause
            } else {
                if (status.playWhenReady || status.isPlaying) DisplayPlayingSource.Playing else DisplayPlayingSource.Pause
            }
        }
    }
}