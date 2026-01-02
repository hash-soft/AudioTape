package com.hashsoft.audiotape.ui.slider

import androidx.compose.ui.graphics.Color

/**
 * [WaveformSlider] で使用される色設定
 *
 * @param thumbColor スライダーのつまみの色
 * @param activeTrackColor スライダーの有効な（左側）トラックの色
 * @param inactiveTrackColor スライダーの無効な（右側）トラックの色
 * @param disabledThumbColor 無効化時のつまみの色
 * @param disabledActiveTrackColor 無効化時の有効なトラックの色
 * @param disabledInactiveTrackColor 無効化時の無効なトラックの色
 */
data class WaveformSliderColors(
    val thumbColor: Color,
    val activeTrackColor: Color,
    val inactiveTrackColor: Color,
    val disabledThumbColor: Color,
    val disabledActiveTrackColor: Color,
    val disabledInactiveTrackColor: Color
)
