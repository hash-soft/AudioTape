package com.hashsoft.audiotape.data

/**
 * オーディオテープ一覧のソート順
 */
enum class AudioTapeListSortOrder {
    /** 名前(昇順) */
    NAME_ASC,

    /** 名前(降順) */
    NAME_DESC,

    /** 最終再生(新しい) */
    LAST_PLAYED_NEW,

    /** 最終再生(古い) */
    LAST_PLAYED_OLD,

    CREATED_NEW,
    CREATED_OLD;


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
