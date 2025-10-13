package com.hashsoft.audiotape.ui

/**
 * テープ設定のコールバック引数
 */
sealed interface TapeSettingsCallbackArgument {

    /**
     * 音量
     *
     * @property volume 音量
     */
    data class Volume(
        val volume: Float
    ) : TapeSettingsCallbackArgument

    /**
     * 再生速度
     *
     * @property speed 再生速度
     */
    data class Speed(
        val speed: Float
    ) : TapeSettingsCallbackArgument

    /**
     * ピッチ
     *
     * @property pitch ピッチ
     */
    data class Pitch(
        val pitch: Float
    ) : TapeSettingsCallbackArgument
}
