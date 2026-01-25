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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme
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
 * @param colors スライダーの色設定
 * @param options スライダーの表示オプション（振幅、周波数、速度など）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaveformSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    enabled: Boolean = true,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: WaveformSliderColors = WaveformSliderDefaults.colors(),
    options: WaveformSliderOptions = WaveformSliderDefaults.options()
) {
    val safeValue = value.coerceIn(0f, 1f)
    // つまみ
    Slider(
        value = safeValue,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        onValueChangeFinished = onValueChangeFinished,
        colors = SliderDefaults.colors(
            thumbColor = colors.thumbColor,
            activeTrackColor = Color.Transparent,
            inactiveTrackColor = Color.Transparent
        ),
        track = {
            val infiniteTransition = rememberInfiniteTransition(label = "wave_animation")

            val baseDuration = 2000
            val duration = if (options.speed > 0) (baseDuration / options.speed).toInt() else Int.MAX_VALUE

            val phase by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 2 * PI.toFloat() * options.speed, // 波の速さ
                animationSpec = infiniteRepeatable(
                    tween(durationMillis = duration, easing = LinearEasing),
                    RepeatMode.Restart
                ), label = "phase"
            )

            Box(modifier = modifier) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val width = size.width
                    val height = size.height
                    val centerY = height / 2
                    val activeTrackWidth = width * safeValue
                    if (isPlaying && options.speed > 0) {
                        // 波線
                        val wavePath = Path()
                        val amplitude = options.amplitude.toPx() // 波の高さ
                        val frequency = options.frequency // 波の間隔

                        val startY = (sin(-phase) * amplitude) + centerY
                        wavePath.moveTo(0f, startY)

                        // 波の描画パス作成
                        for (x in 1..activeTrackWidth.toInt()) {
                            val y = (sin(x * frequency - phase) * amplitude) + centerY
                            wavePath.lineTo(x.toFloat(), y)
                        }
                        drawPath(
                            path = wavePath,
                            color = if (enabled) colors.activeTrackColor else colors.disabledActiveTrackColor,
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )
                    } else {
                        // 直線
                        drawLine(
                            color = if (enabled) colors.activeTrackColor else colors.disabledActiveTrackColor,
                            start = Offset(0f, centerY),
                            end = Offset(activeTrackWidth, centerY),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                    // 右側の未再生の線
                    drawLine(
                        color = if (enabled) colors.inactiveTrackColor else colors.disabledInactiveTrackColor,
                        start = Offset(activeTrackWidth, centerY),
                        end = Offset(width, centerY),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun WaveformSliderPlayingPreview() {
    val value = remember { mutableFloatStateOf(0.2f) }
    AudioTapeTheme {
        WaveformSlider(
            value = value.floatValue,
            onValueChange = { value.floatValue = it },
            isPlaying = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WaveformSliderStoppedPreview() {
    val value = remember { mutableFloatStateOf(0.5f) }
    AudioTapeTheme {
        WaveformSlider(
            value = value.floatValue,
            onValueChange = { value.floatValue = it },
            isPlaying = false,
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )
    }
}
