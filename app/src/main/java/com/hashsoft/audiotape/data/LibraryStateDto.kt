package com.hashsoft.audiotape.data

/**
 * ライブラリ画面の状態を保持するデータ転送オブジェクト(DTO)
 *
 * @param selectedTabIndex 選択されているタブのインデックス
 */
data class LibraryStateDto(
    val selectedTabIndex: Int,
    val tapeListSortOrder: AudioTapeListSortOrder = AudioTapeListSortOrder.NAME_ASC
)
