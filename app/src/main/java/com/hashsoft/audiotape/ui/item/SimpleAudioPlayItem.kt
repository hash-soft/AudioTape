package com.hashsoft.audiotape.ui.item

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import com.hashsoft.audiotape.ui.AudioCallbackArgument
import com.hashsoft.audiotape.ui.AudioCallbackResult

@Composable
fun SimpleAudioPlayItem(
    directory: String,
    name: String,
    isReadyOk: Boolean = false,
    isPlaying: Boolean = false,
    durationMs: Long = 0,
    contentPosition: Long = 0,
    audioCallback: (AudioCallbackArgument) -> AudioCallbackResult = { AudioCallbackResult.None }
) {
    val configuration = LocalConfiguration.current
    if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
        SimpleAudioPlayItemPortrait(
            directory,
            name,
            isReadyOk,
            isPlaying,
            durationMs,
            contentPosition,
            audioCallback
        )
    } else {
        // Todo 横向き用に変更
        SimpleAudioPlayItemPortrait(
            directory,
            name,
            isReadyOk,
            isPlaying,
            durationMs,
            contentPosition,
            audioCallback
        )
    }
}
