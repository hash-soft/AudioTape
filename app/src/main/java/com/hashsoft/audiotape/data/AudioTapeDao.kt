package com.hashsoft.audiotape.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * オーディオテープの再生位置
 *
 * @property folderPath フォルダパス
 * @property currentName 現在のファイル名
 * @property position 再生位置
 * @property updateTime 更新日時
 */
data class AudioTapePlayingPosition(
    @ColumnInfo(name = "folder_path") val folderPath: String,
    @ColumnInfo(name = "current_name") val currentName: String,
    @ColumnInfo(name = "position") val position: Long,
    @ColumnInfo(name = "update_time") val updateTime: Long
)

/**
 * オーディオテープの音量
 *
 * @property folderPath フォルダパス
 * @property volume 音量
 * @property updateTime 更新日時
 */
data class AudioTapeVolume(
    @ColumnInfo(name = "folder_path") val folderPath: String,
    @ColumnInfo(name = "volume") val volume: Float,
    @ColumnInfo(name = "update_time") val updateTime: Long
)

/**
 * オーディオテープの再生速度
 *
 * @property folderPath フォルダパス
 * @property speed 再生速度
 * @property updateTime 更新日時
 */
data class AudioTapeSpeed(
    @ColumnInfo(name = "folder_path") val folderPath: String,
    @ColumnInfo(name = "speed") val speed: Float,
    @ColumnInfo(name = "update_time") val updateTime: Long
)

/**
 * オーディオテープのピッチ
 *
 * @property folderPath フォルダパス
 * @property pitch ピッチ
 * @property updateTime 更新日時
 */
data class AudioTapePitch(
    @ColumnInfo(name = "folder_path") val folderPath: String,
    @ColumnInfo(name = "pitch") val pitch: Float,
    @ColumnInfo(name = "update_time") val updateTime: Long
)


/**
 * オーディオテープDAO
 */
@Dao
interface AudioTapeDao {
    /**
     * すべてのオーディオテープを取得する
     *
     * @return オーディオテープのリスト
     */
    @Query("SELECT * FROM audio_tape")
    fun getAll(): Flow<List<AudioTapeEntity>>

    /**
     * パスでオーディオテープを検索する
     *
     * @param path パス
     * @return オーディオテープ
     */
    @Query("SELECT * FROM audio_tape WHERE folder_path = :path")
    fun findByPath(path: String): Flow<AudioTapeEntity?>

    /**
     * オーディオテープを挿入する
     *
     * @param entity オーディオテープ
     * @return 挿入した行ID
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entity: AudioTapeEntity): Long

    /**
     * 再生位置を更新する
     *
     * @param entity 再生位置
     */
    @Update(entity = AudioTapeEntity::class)
    suspend fun updatePlayingPosition(entity: AudioTapePlayingPosition)

    /**
     * 音量を更新する
     *
     * @param entity 音量
     */
    @Update(entity = AudioTapeEntity::class)
    suspend fun updateVolume(entity: AudioTapeVolume)

    /**
     * 再生速度を更新する
     *
     * @param entity 再生速度
     */
    @Update(entity = AudioTapeEntity::class)
    suspend fun updateSpeed(entity: AudioTapeSpeed)

    /**
     * ピッチを更新する
     *
     * @param entity ピッチ
     */
    @Update(entity = AudioTapeEntity::class)
    suspend fun updatePitch(entity: AudioTapePitch)

}
