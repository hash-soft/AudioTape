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
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val defaultSortOrder: AudioTapeSortOrder = AudioTapeSortOrder.NAME_ASC,
    val defaultRepeat: Boolean = true,
    val defaultVolume: Float = 1.0f,
    val defaultSpeed: Float = 1.0f,
    val defaultPitch: Float = 1.0f,
    val screenRestore: Boolean = true,
    val rewindingSpeed: Int = 0,
    val forwardingSpeed: Int = 0
)