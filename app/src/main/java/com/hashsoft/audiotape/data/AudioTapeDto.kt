package com.hashsoft.audiotape.data

/**
 * オーディオテープのソート順
 */
enum class AudioTapeSortOrder {
    /** 名前(昇順) */
    NAME_ASC,

    /** 名前(降順) */
    NAME_DESC,

    /** 日付(昇順) */
    DATE_ASC,

    /** 日付(降順) */
    DATE_DESC,

    /** そのまま */
    ASIS;

    companion object {
        /**
         * Int値から[AudioTapeSortOrder]に変換する
         *
         * @param code Int値
         * @return [AudioTapeSortOrder]
         */
        fun fromInt(code: Int): AudioTapeSortOrder {
            return entries.find { it.ordinal == code } ?: NAME_ASC
        }
    }
}

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
    val createTime: Long = 0,
    val updateTime: Long = 0
)
