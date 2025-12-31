package com.hashsoft.audiotape.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
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
 * オーディオテープの再生位置と最終再生日時
 *
 * @property folderPath フォルダパス
 * @property currentName 現在のファイル名
 * @property position 再生位置
 * @property lastPlayedAt 最終再生日時
 * @property updateTime 更新日時
 */
data class AudioTapePlayingPositionWithLastPlayedAt(
    @ColumnInfo(name = "folder_path") val folderPath: String,
    @ColumnInfo(name = "current_name") val currentName: String,
    @ColumnInfo(name = "position") val position: Long,
    @ColumnInfo(name = "last_played_at") val lastPlayedAt: Long,
    @ColumnInfo(name = "update_time") val updateTime: Long
)

/**
 * オーディオテープのソート順サブエンティティ
 *
 * @property folderPath フォルダパス
 * @property sortOrder ソート順
 * @property updateTime 更新日時
 */
data class AudioTapeSortOrderSubEntity(
    @ColumnInfo(name = "folder_path") val folderPath: String,
    @ColumnInfo(name = "sort_order") val sortOrder: Int,
    @ColumnInfo(name = "update_time") val updateTime: Long
)

/**
 * オーディオテープのリピート設定
 *
 * @property folderPath フォルダパス
 * @property repeat リピート設定
 * @property updateTime 更新日時
 */
data class AudioTapeRepeat(
    @ColumnInfo(name = "folder_path") val folderPath: String,
    @ColumnInfo(name = "repeat") val repeat: Int,
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
 * オーディオテープのプライマリキー情報
 *
 * @property folderPath フォルダパス
 */
data class AudioTapePrimary(
    @ColumnInfo(name = "folder_path") val folderPath: String,
)

/**
 * オーディオテープDAO
 */
@Dao
interface AudioTapeDao {

    /**
     * すべてのオーディオテープを名前の昇順で取得する
     *
     * @return オーディオテープのリスト
     */
    @Query("SELECT * FROM audio_tape ORDER BY folder_path DESC")
    fun getAllByNameAsc(): Flow<List<AudioTapeEntity>>

    /**
     * すべてのオーディオテープを名前の降順で取得する
     *
     * @return オーディオテープのリスト
     */
    @Query("SELECT * FROM audio_tape ORDER BY folder_path ASC")
    fun getAllByNameDesc(): Flow<List<AudioTapeEntity>>

    /**
     * すべてのオーディオテープを最終再生日時の昇順で取得する
     *
     * @return オーディオテープのリスト
     */
    @Query("SELECT * FROM audio_tape ORDER BY last_played_at DESC")
    fun getAllByLastPlayedAsc(): Flow<List<AudioTapeEntity>>

    /**
     * すべてのオーディオテープを最終再生日時の降順で取得する
     *
     * @return オーディオテープのリスト
     */
    @Query("SELECT * FROM audio_tape ORDER BY last_played_at ASC")
    fun getAllByLastPlayedDesc(): Flow<List<AudioTapeEntity>>

    /**
     * すべてのオーディオテープを作成日時の昇順で取得する
     *
     * @return オーディオテープのリスト
     */
    @Query("SELECT * FROM audio_tape ORDER BY create_time DESC")
    fun getAllByCreatedAsc(): Flow<List<AudioTapeEntity>>

    /**
     * すべてのオーディオテープを作成日時の降順で取得する
     *
     * @return オーディオテープのリスト
     */
    @Query("SELECT * FROM audio_tape ORDER BY create_time ASC")
    fun getAllByCreatedDesc(): Flow<List<AudioTapeEntity>>

    /**
     * パスでオーディオテープを検索する
     *
     * @param path パス
     * @return オーディオテープ
     */
    @Query("SELECT * FROM audio_tape WHERE folder_path = :path")
    fun findByPath(path: String): Flow<AudioTapeEntity?>

    /**
     * パスでsortOrderを検索する
     *
     * @param path パス
     * @return sortOrder
     */
    @Query("SELECT sort_order FROM audio_tape WHERE folder_path = :path")
    fun findSortOrderByPath(path: String): Flow<Int?>

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
     * 再生位置と最終再生日時を更新する
     *
     * @param entity 再生位置と最終再生日時
     */
    @Update(entity = AudioTapeEntity::class)
    suspend fun updatePlayingPositionWithLastPlayedAt(entity: AudioTapePlayingPositionWithLastPlayedAt)

    /**
     * ソート順を更新する
     *
     * @param entity ソート順
     */
    @Update(entity = AudioTapeEntity::class)
    suspend fun updateSortOrder(entity: AudioTapeSortOrderSubEntity)

    /**
     * リピート設定を更新する
     *
     * @param entity リピート設定
     */
    @Update(entity = AudioTapeEntity::class)
    suspend fun updateRepeat(entity: AudioTapeRepeat)

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

    /**
     * オーディオテープを削除する
     *
     * @param entities 削除するオーディオテープ
     */
    @Delete(entity = AudioTapeEntity::class)
    suspend fun deleteTapes(vararg entities: AudioTapePrimary)

}
