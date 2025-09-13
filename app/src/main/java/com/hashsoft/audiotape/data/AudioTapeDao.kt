package com.hashsoft.audiotape.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

data class AudioTapePosition(
    @ColumnInfo(name = "folder_path") val folderPath: String,
    @ColumnInfo(name = "position") val position: Long
)

data class AudioTapeNotNull(
    @ColumnInfo(name = "folder_path") val folderPath: String,
    @ColumnInfo(name = "current_name") val currentName: String,
    @ColumnInfo(name = "position") val position: Long
)

@Dao
interface AudioTapeDao {
    @Query("SELECT * FROM audio_tape")
    fun getAll(): Flow<List<AudioTapeEntity>>

    @Query("SELECT * FROM audio_tape WHERE folder_path = :path")
    fun findByPath(path: String): Flow<AudioTapeEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg entity: AudioTapeEntity)

    @Upsert
    suspend fun upsertAll(vararg entity: AudioTapeEntity)

    @Upsert(entity = AudioTapeEntity::class)
    suspend fun upsertNotNull(vararg entity: AudioTapeNotNull)

    @Update(entity = AudioTapeEntity::class)
    suspend fun updatePosition(vararg entity: AudioTapePosition)

    @Update(entity = AudioTapeEntity::class)
    suspend fun updateNotNull(vararg entity: AudioTapeNotNull)

}
