package com.hashsoft.audiotape.ui


sealed interface UserSettingsCallbackArgument {

    data class Theme(
        val theme: Int
    ) : UserSettingsCallbackArgument

    data class SortOrder(
        val sortOrder: Int
    ) : UserSettingsCallbackArgument

    data class Repeat(
        val repeat: Boolean
    ) : UserSettingsCallbackArgument

    data class Volume(
        val volume: Float
    ) : UserSettingsCallbackArgument

    data class Speed(
        val speed: Float
    ) : UserSettingsCallbackArgument

    data class Pitch(
        val pitch: Float
    ) : UserSettingsCallbackArgument

}
