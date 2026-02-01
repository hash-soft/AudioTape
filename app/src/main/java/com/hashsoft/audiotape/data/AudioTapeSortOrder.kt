package com.hashsoft.audiotape.data

/**
 * オーディオテープのソート順
 */
enum class AudioTapeSortOrder {
    /** ファイル名(昇順) */
    NAME_ASC,

    /** ファイル名(降順) */
    NAME_DESC,

    ARTIST_ASC,

    ARTIST_DESC,

    TITLE_ASC,

    TITLE_DESC,

    ALBUM_ASC,

    ALBUM_DESC;

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
