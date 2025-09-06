package com.hashsoft.audiotape.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "theme_mode") val themeMode: String? = null,
    @ColumnInfo(name = "screen_restore") val screenRestore: Boolean? = false,
    @ColumnInfo(name = "rewinding_speed") val rewindingSpeed: Float? = 1.0f,
    @ColumnInfo(name = "forwarding_speed") val forwardingSpeed: Float? = 1.0f
)
