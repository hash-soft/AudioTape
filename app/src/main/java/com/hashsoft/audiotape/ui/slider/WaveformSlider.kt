package com.hashsoft.audiotape.ui.slider

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

/**
 * 波形を表示するスライダー
 *
 * @param value スライダーの現在の値
 * @param onValueChange スライダーの値が変更されたときにトリガーされるコールバック
 * @param modifier スライダーに適用される修飾子
 * @param isPlaying 音声が現在再生中かどうか
 * @param enabled スライダーが有効かどうか
 * @param onValueChangeFinished ユーザーがスライダーの値の変更を終了したときにトリガーされるコールバック
 * @param waveColor 波形の色
 * @param trackColor トラックの色
 * @param thumbColor つまみの色
 * @param waveAmplitude 波形の振幅
 */
@Composable
fun WaveformSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    enabled: Boolean = true,
    onValueChangeFinished: (() -> Unit)? = null,
    waveColor: Color = MaterialTheme.colorScheme.secondary,
    trackColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f),
    thumbColor: Color = MaterialTheme.colorScheme.secondary,
    waveAmplitude: Dp = 6.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_animation")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 1000, easing = LinearEasing),
            RepeatMode.Restart
        ), label = ""
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val width = size.width
            val height = size.height
            val centerY = height / 2

            val activeTrackWidth = width * value

            // Draw inactive track
            drawLine(
                color = trackColor,
                start = Offset(activeTrackWidth, centerY),
                end = Offset(width, centerY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            if (isPlaying) {
                // Draw wave for the active part
                val wavePath = Path()
                val amplitude = waveAmplitude.toPx() // Amplitude of the wave
                val frequency = 0.04f // Frequency of the wave

                val startY = (sin(phase) * amplitude) + centerY
                wavePath.moveTo(0f, startY)

                for (x in 1..activeTrackWidth.toInt()) {
                    // Sine wave equation with phase shift for animation
                    val y = (sin(x * frequency + phase) * amplitude) + centerY
                    wavePath.lineTo(x.toFloat(), y)
                }
                drawPath(
                    path = wavePath,
                    color = waveColor,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )
            } else {
                // Draw straight line for the active part
                drawLine(
                    color = waveColor,
                    start = Offset(0f, centerY),
                    end = Offset(activeTrackWidth, centerY),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            onValueChangeFinished = onValueChangeFinished,
            colors = SliderDefaults.colors(
                thumbColor = thumbColor,
                activeTrackColor = Color.Transparent,
                inactiveTrackColor = Color.Transparent
            )
        )
    }
}


@Preview(showBackground = true)
@Composable
fun WaveformSliderPlayingPreview() {
    var value by remember { mutableFloatStateOf(0.5f) }
    WaveformSlider(
        value = value,
        onValueChange = { value = it },
        isPlaying = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun WaveformSliderStoppedPreview() {
    var value by remember { mutableFloatStateOf(0.5f) }
    WaveformSlider(
        value = value,
        onValueChange = { value = it },
        isPlaying = false,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    )
}
