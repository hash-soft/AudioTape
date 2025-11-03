package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hashsoft.audiotape.logic.TimeFormat.Companion.formatMillis
import com.hashsoft.audiotape.ui.slider.WaveformSlider

/**
 * 再生を制御するためのスライダーを表示するコンポーザブル
 *
 * @param isPlaying メディアが現在再生中かどうか
 * @param enabled スライダーが有効かどうか
 * @param contentPosition メディアの現在位置（ミリ秒）
 * @param durationMs メディアの総再生時間（ミリ秒）
 * @param onChanged ユーザーがスライダーの値を変更したときに呼び出されるコールバック
 */
@Composable
fun PlaySliderItem(
    isPlaying: Boolean,
    enabled: Boolean,
    contentPosition: Long,
    durationMs: Long,
    onChanged: (Long) -> Unit
) {
    var sliderPosition by remember { mutableStateOf<Float?>(null) }

    val currentPosition = sliderPosition?.let { (it * durationMs).toLong() } ?: contentPosition
    val currentValue = if (durationMs > 0) {
        (sliderPosition ?: (contentPosition.toFloat() / durationMs))
    } else {
        0f
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        WaveformSlider(
            value = currentValue,
            onValueChange = {
                sliderPosition = it
            },
            onValueChangeFinished = {
                sliderPosition?.let {
                    onChanged((it * durationMs).toLong())
                }
                sliderPosition = null
            },
            isPlaying = isPlaying,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp),
            waveAmplitude = 4.dp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = formatMillis(currentPosition))
            Text(text = formatMillis(durationMs))
        }
    }
}
