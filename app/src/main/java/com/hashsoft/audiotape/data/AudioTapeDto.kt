package com.hashsoft.audiotape.data

enum class AudioTapeSortOrder {
    NAME_ASC,
    NAME_DESC,
    DATE_ASC,
    DATE_DESC;

    companion object {
        fun fromInt(code: Int): AudioTapeSortOrder {
            return entries.find { it.ordinal == code } ?: NAME_ASC
        }
    }
}

data class AudioTapeDto(
    val folderPath: String,
    val currentName: String,
    val position: Long,
    val sortOrder: AudioTapeSortOrder = AudioTapeSortOrder.NAME_ASC,
    val speed: Float = 1.0f
)
