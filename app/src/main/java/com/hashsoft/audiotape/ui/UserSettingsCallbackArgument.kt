package com.hashsoft.audiotape.ui


sealed interface UserSettingsCallbackArgument {

    data class Theme(
        val theme: Int
    ) : UserSettingsCallbackArgument


}
