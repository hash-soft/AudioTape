package com.hashsoft.audiotape.ui.bar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.galaxygoldfish.waveslider.WaveSlider
import com.galaxygoldfish.waveslider.WaveSliderDefaults
import com.hashsoft.audiotape.logic.TimeFormat.Companion.formatMillis
import com.hashsoft.audiotape.ui.AudioCallbackArgument
import com.hashsoft.audiotape.ui.AudioCallbackResult
import kotlinx.coroutines.delay
import timber.log.Timber

@Composable
fun AudioSeekbar(
    position: Long = 0,
    durationMs: Long = 0,
    isPlaying: Boolean = false,
    enabled: Boolean = true,
    audioCallback: (AudioCallbackArgument) -> AudioCallbackResult = { AudioCallbackResult.None },
) {
    var contentPosition by remember(position) { mutableLongStateOf(position) }
    var sliderDown by remember { mutableStateOf(false) }

    if (isPlaying) {
        LaunchedEffect( position) {
            repeat(Int.MAX_VALUE) {
                if(!sliderDown) {
                    val result = audioCallback(AudioCallbackArgument.Position)
                    if (result is AudioCallbackResult.Position) {
                        contentPosition = result.position
                    }
                }
                delay(1000)
            }
        }
    }

    // valueの範囲が0～1なので 現在位置 / 総時間 にする必要がある
    Column(modifier = Modifier.fillMaxWidth()) {
        WaveSlider(
            value = if (durationMs == 0L) 0f else contentPosition.toFloat() / durationMs,
            onValueChange = {
                sliderDown = true
                contentPosition = (it * durationMs).toLong()
            },
            onValueChangeFinished = {
                sliderDown = false
                audioCallback(AudioCallbackArgument.SeekTo(contentPosition))
            },
            animationOptions = WaveSliderDefaults.animationOptions(
                reverseDirection = false,
                flatlineOnDrag = true,
                animateWave = false,
                reverseFlatline = false
            ),
            colors = WaveSliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary
            ),
            waveOptions = if (isPlaying) WaveSliderDefaults.waveOptions() else WaveSliderDefaults.waveOptions(
                amplitude = 0F,
                frequency = 0F,
            ),
            modifier = Modifier.padding(
                //horizontal = 20.dp,
                //vertical = 50.dp
            ), enabled = enabled
        )
        Row {
            Text(
                text = formatMillis(contentPosition),
                modifier = Modifier.weight(1f)
            )
            Text(text = formatMillis(durationMs))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SeekbarPreview() {
    AudioSeekbar()
}