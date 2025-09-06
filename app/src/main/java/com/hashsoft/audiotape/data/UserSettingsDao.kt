package com.hashsoft.audiotape.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

data class UidAndUserThemeMode(
    val uid: Int,
    @ColumnInfo(name = "theme_mode") val themeMode: String,
)

data class UidAndScreenRestore(
    val uid: Int,
    @ColumnInfo(name = "screen_restore") val screenRestore: Boolean,
)

data class UidAndRewindingSpeed(
    val uid: Int,
    @ColumnInfo(name = "rewinding_speed") val rewindingSpeed: Float,
)

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings WHERE uid = :id")
    fun findById(id: Int): Flow<UserSettingsEntity?>

    @Query("SELECT theme_mode FROM user_settings WHERE uid = :id")
    fun getThemeMode(id: Int): Flow<String?>

    @Upsert
    suspend fun upsertAll(vararg userSettings: UserSettingsEntity)

    @Upsert(entity = UserSettingsEntity::class)
    suspend fun upsertThemeMode(uidUserThemeMode: UidAndUserThemeMode)

    @Upsert(entity = UserSettingsEntity::class)
    suspend fun upsertScreenRestore(uidScreenRestore: UidAndScreenRestore)

    @Upsert(entity = UserSettingsEntity::class)
    suspend fun upsertRewindingSpeed(uidRewindingSpeed: UidAndRewindingSpeed)
}