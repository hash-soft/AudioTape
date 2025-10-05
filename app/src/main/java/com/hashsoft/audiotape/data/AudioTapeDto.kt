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
    val position: Long = 0,
    val tapeName: String = "",
    val sortOrder: AudioTapeSortOrder = AudioTapeSortOrder.NAME_ASC,
    val repeat: Boolean = true,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val pitch: Float = 1.0f,
    val itemCount: Int = 0,
    val totalTime: Long = 0,
    val createTime: Long = 0,
    val updateTime: Long = 0
)
