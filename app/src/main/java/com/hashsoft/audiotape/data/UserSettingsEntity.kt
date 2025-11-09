package com.hashsoft.audiotape.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "theme_mode", defaultValue = "SYSTEM") val themeMode: String = "SYSTEM",
    @ColumnInfo(name = "default_sort_order", defaultValue = "0") val defaultSortOrder: Int = 0,
    @ColumnInfo(name = "default_repeat", defaultValue = "2") val defaultRepeat: Int = 2,
    @ColumnInfo(name = "default_volume", defaultValue = "1.0") val defaultVolume: Float = 1.0f,
    @ColumnInfo(name = "default_speed", defaultValue = "1.0") val defaultSpeed: Float = 1.0f,
    @ColumnInfo(name = "default_pitch", defaultValue = "1.0") val defaultPitch: Float = 1.0f,
    @ColumnInfo(name = "screen_restore", defaultValue = "true") val screenRestore: Boolean = true,
    @ColumnInfo(name = "rewinding_speed", defaultValue = "0") val rewindingSpeed: Int = 0,
    @ColumnInfo(name = "forwarding_speed", defaultValue = "0") val forwardingSpeed: Int = 0,
    @ColumnInfo(name = "create_time") val createTime: Long,
    @ColumnInfo(name = "update_time") val updateTime: Long
)
