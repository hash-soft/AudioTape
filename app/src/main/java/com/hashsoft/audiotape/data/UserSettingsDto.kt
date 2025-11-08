package com.hashsoft.audiotape.data

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK;

    companion object {
        /**
         * Int値から[ThemeMode]に変換する
         *
         * @param code Int値
         * @return [ThemeMode]
         */
        fun fromInt(code: Int): ThemeMode {
            return ThemeMode.entries.find { it.ordinal == code } ?: SYSTEM
        }
    }
}

data class UserSettingsDto(
    val uid: Int,
    val themeMode: ThemeMode,
    val screenRestore: Boolean,
    val rewindingSpeed: Float,
    val forwardingSpeed: Float
)