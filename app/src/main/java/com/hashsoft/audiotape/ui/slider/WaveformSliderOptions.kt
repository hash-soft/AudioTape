package com.hashsoft.audiotape.ui.slider

import androidx.compose.ui.unit.Dp

/**
 * [WaveformSlider] の表示オプション
 *
 * @param amplitude 波形の振幅（高さ）
 * @param frequency 波形の周波数（波の密度）
 * @param speed 波形のアニメーション速度
 */
data class WaveformSliderOptions(
    val amplitude: Dp,
    val frequency: Float,
    val speed: Float
)
