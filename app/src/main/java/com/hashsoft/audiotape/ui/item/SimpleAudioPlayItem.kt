package com.hashsoft.audiotape.ui.item

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import com.hashsoft.audiotape.data.DisplayPlayingSource
import com.hashsoft.audiotape.ui.AudioCallbackArgument

@Composable
fun SimpleAudioPlayItem(
    directory: String,
    name: String,
    isAvailable: Boolean = false,
    displayPlaying: DisplayPlayingSource,
    durationMs: Long = 0,
    contentPosition: Long = 0,
    enableTransfer: Boolean = true,
    audioCallback: (AudioCallbackArgument) -> Unit
) {
    val configuration = LocalConfiguration.current
    if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
        SimpleAudioPlayItemPortrait(
            directory,
            name,
            isAvailable,
            displayPlaying,
            durationMs,
            contentPosition,
            enableTransfer,
            audioCallback
        )
    } else {
        // Todo 横向き用に変更
        SimpleAudioPlayItemPortrait(
            directory,
            name,
            isAvailable,
            displayPlaying,
            durationMs,
            contentPosition,
            enableTransfer,
            audioCallback
        )
    }
}
