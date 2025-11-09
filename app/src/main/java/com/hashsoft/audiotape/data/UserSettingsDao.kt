package com.hashsoft.audiotape.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

data class UserSettingsEntityThemeMode(
    val uid: Int,
    @ColumnInfo(name = "theme_mode") val themeMode: String,
)

data class UserSettingsEntityDefaultSortOrder(
    val uid: Int,
    @ColumnInfo(name = "default_sort_order") val defaultSortOrder: Int,
)

data class UserSettingsEntityDefaultRepeat(
    val uid: Int,
    @ColumnInfo(name = "default_repeat") val defaultRepeat: Int,
)

data class UserSettingsEntityDefaultVolume(
    val uid: Int,
    @ColumnInfo(name = "default_volume") val defaultVolume: Float,
)

data class UserSettingsEntityDefaultSpeed(
    val uid: Int,
    @ColumnInfo(name = "default_speed") val defaultSpeed: Float,
)

data class UserSettingsEntityDefaultPitch(
    val uid: Int,
    @ColumnInfo(name = "default_pitch") val defaultPitch: Float,
)

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings WHERE uid = :id")
    fun findById(id: Int): Flow<UserSettingsEntity?>

    @Query("SELECT theme_mode FROM user_settings WHERE uid = :id")
    fun getThemeMode(id: Int): Flow<String?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(userSettings: UserSettingsEntity): Long

    @Update(entity = UserSettingsEntity::class)
    suspend fun updateThemeMode(uidUserThemeMode: UserSettingsEntityThemeMode)

    @Update(entity = UserSettingsEntity::class)
    suspend fun updateDefaultSortOrder(uidUserThemeMode: UserSettingsEntityDefaultSortOrder)

    @Update(entity = UserSettingsEntity::class)
    suspend fun updateDefaultRepeat(uidUserThemeMode: UserSettingsEntityDefaultRepeat)

    @Update(entity = UserSettingsEntity::class)
    suspend fun updateDefaultVolume(uidUserThemeMode: UserSettingsEntityDefaultVolume)

    @Update(entity = UserSettingsEntity::class)
    suspend fun updateDefaultSpeed(uidUserThemeMode: UserSettingsEntityDefaultSpeed)

    @Update(entity = UserSettingsEntity::class)
    suspend fun updateDefaultPitch(uidUserThemeMode: UserSettingsEntityDefaultPitch)

}
