package com.hashsoft.audiotape.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * オーディオテープの情報を格納するエンティティ
 *
 * @param folderPath フォルダのパス(主キー)
 * @param currentName 現在再生中のアイテム名
 * @param position 再生位置
 * @param tapeName テープ名
 * @param sortOrder ソート順
 * @param repeat リピート設定
 * @param volume 音量
 * @param speed 再生速度
 * @param pitch ピッチ
 * @param itemCount アイテム数
 * @param totalTime 総再生時間
 * @param createTime 作成日時
 * @param updateTime 更新日時
 */
@Entity(tableName = "audio_tape")
data class AudioTapeEntity(
    @PrimaryKey @ColumnInfo(name = "folder_path") val folderPath: String,
    @ColumnInfo(name = "current_name") val currentName: String,
    @ColumnInfo(name = "position", defaultValue = "0") val position: Long = 0,
    @ColumnInfo(name = "tape_name", defaultValue = "") val tapeName: String = "",
    @ColumnInfo(name = "sort_order", defaultValue = "0") val sortOrder: Int = 0,
    @ColumnInfo(name = "repeat", defaultValue = "true") val repeat: Boolean = true,
    @ColumnInfo(name = "volume", defaultValue = "1.0") val volume: Float = 1.0f,
    @ColumnInfo(name = "speed", defaultValue = "1.0") val speed: Float = 1.0f,
    @ColumnInfo(name = "pitch", defaultValue = "1.0") val pitch: Float = 1.0f,
    @ColumnInfo(name = "item_count", defaultValue = "0") val itemCount: Int = 0,
    @ColumnInfo(name = "total_time", defaultValue = "0") val totalTime: Long = 0,
    @ColumnInfo(name = "create_time") val createTime: Long,
    @ColumnInfo(name = "update_time") val updateTime: Long
)
