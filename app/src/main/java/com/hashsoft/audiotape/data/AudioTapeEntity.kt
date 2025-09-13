package com.hashsoft.audiotape.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio_tape")
data class AudioTapeEntity(
    @PrimaryKey @ColumnInfo(name = "folder_path") val folderPath: String,
    @ColumnInfo(name = "current_name") val currentName: String,
    @ColumnInfo(name = "position") val position: Long,
    @ColumnInfo(name = "sort_order") val sortOrder: Int? = 0,
    @ColumnInfo(name = "speed") val speed: Float? = 1.0f
)

// 個数、トータル時間、更新時間も追加か
// どこまで再生したかもわかるようにしたいが
