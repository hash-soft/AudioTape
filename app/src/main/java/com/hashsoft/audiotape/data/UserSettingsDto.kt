package com.hashsoft.audiotape.data

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

data class UserSettingsDto (
    val uid: Int,
    val themeMode: ThemeMode,
    val screenRestore: Boolean,
    val rewindingSpeed: Float,
    val forwardingSpeed: Float
)