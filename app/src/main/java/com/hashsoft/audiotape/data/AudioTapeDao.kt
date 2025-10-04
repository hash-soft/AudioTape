package com.hashsoft.audiotape.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

data class AudioTapePlayingPosition(
    @ColumnInfo(name = "folder_path") val folderPath: String,
    @ColumnInfo(name = "current_name") val currentName: String,
    @ColumnInfo(name = "position") val position: Long,
    @ColumnInfo(name = "update_time") val updateTime: Long
)

@Dao
interface AudioTapeDao {
    @Query("SELECT * FROM audio_tape")
    fun getAll(): Flow<List<AudioTapeEntity>>

    @Query("SELECT * FROM audio_tape WHERE folder_path = :path")
    fun findByPath(path: String): Flow<AudioTapeEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entity: AudioTapeEntity): Long

    @Update(entity = AudioTapeEntity::class)
    suspend fun updatePlayingPosition(entity: AudioTapePlayingPosition)

}
