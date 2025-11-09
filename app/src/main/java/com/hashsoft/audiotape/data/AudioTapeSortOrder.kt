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
