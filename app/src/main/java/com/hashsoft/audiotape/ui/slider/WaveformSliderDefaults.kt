package com.hashsoft.audiotape.ui.slider

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * [WaveformSlider] のデフォルト値を管理するオブジェクト
 */
object WaveformSliderDefaults {

    /**
     * [WaveformSlider] のデフォルトの色設定を生成する
     *
     * @param thumbColor スライダーのつまみの色
     * @param activeTrackColor スライダーの有効なトラックの色
     * @param inactiveTrackColor スライダーの無効なトラックの色
     * @param disabledThumbColor 無効化時のつまみの色
     * @param disabledActiveTrackColor 無効化時の有効なトラックの色
     * @param disabledInactiveTrackColor 無効化時の無効なトラックの色
     */
    @Composable
    fun colors(
        thumbColor: Color = MaterialTheme.colorScheme.primary,
        activeTrackColor: Color = MaterialTheme.colorScheme.primary,
        inactiveTrackColor: Color = MaterialTheme.colorScheme.primaryContainer,
        disabledThumbColor: Color = MaterialTheme.colorScheme.secondaryContainer,
        disabledActiveTrackColor: Color = MaterialTheme.colorScheme.secondaryContainer,
        disabledInactiveTrackColor: Color = MaterialTheme.colorScheme.primaryContainer.copy(0.5F)
    ): WaveformSliderColors = WaveformSliderColors(
        thumbColor = thumbColor,
        activeTrackColor = activeTrackColor,
        inactiveTrackColor = inactiveTrackColor,
        disabledThumbColor = disabledThumbColor,
        disabledActiveTrackColor = disabledActiveTrackColor,
        disabledInactiveTrackColor = disabledInactiveTrackColor
    )

    /**
     * [WaveformSlider] のデフォルトの表示オプションを生成する
     *
     * @param amplitude 波形の振幅（高さ）
     * @param frequency 波形の周波数（波の密度）
     * @param speed 波形のアニメーション速度
     */
    @Composable
    fun options(
        amplitude: Dp = 12.dp,
        frequency: Float = 0.06F,
        speed: Float = 1F
    ): WaveformSliderOptions = WaveformSliderOptions(
        amplitude = amplitude,
        frequency = frequency,
        speed = speed
    )

}
