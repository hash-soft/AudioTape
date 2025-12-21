package com.hashsoft.audiotape.data

/**
 * オーディオテープ一覧のソート順
 */
enum class AudioTapeListSortOrder {
    /** 名前(昇順) */
    NAME_ASC,

    /** 名前(降順) */
    NAME_DESC,

    /** 最終再生(昇順) */
    LAST_PLAYED_ASC,

    /** 最終再生(降順) */
    LAST_PLAYED_DESC,

    CREATED_ASC,
    CREATED_DESC;


    companion object {
        /**
         * Int値から[AudioTapeListSortOrder]に変換する
         *
         * @param code Int値
         * @return [AudioTapeListSortOrder]
         */
        fun fromInt(code: Int): AudioTapeListSortOrder {
            return entries.find { it.ordinal == code } ?: NAME_ASC
        }
    }
}
