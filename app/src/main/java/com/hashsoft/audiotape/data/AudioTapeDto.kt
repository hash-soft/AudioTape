package com.hashsoft.audiotape.data


/**
 * オーディオテープのデータ転送オブジェクト(DTO)
 *
 * @param folderPath フォルダのパス
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
data class AudioTapeDto(
    val folderPath: String,
    val currentName: String,
    val position: Long = 0,
    val tapeName: String = "",
    val sortOrder: AudioTapeSortOrder = AudioTapeSortOrder.NAME_ASC,
    val repeat: Boolean = true,
    val volume: Float = 1.0f,
    val speed: Float = 1.0f,
    val pitch: Float = 1.0f,
    val itemCount: Int = 0,
    val totalTime: Long = 0,
    val lastPlayedAt: Long = 0,
    val createTime: Long = 0,
    val updateTime: Long = 0
)
