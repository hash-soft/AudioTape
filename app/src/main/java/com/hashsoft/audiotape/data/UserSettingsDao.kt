package com.hashsoft.audiotape.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

data class UidAndUserThemeMode(
    val uid: Int,
    @ColumnInfo(name = "theme_mode") val themeMode: String,
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
    suspend fun updateThemeMode(uidUserThemeMode: UidAndUserThemeMode)

}