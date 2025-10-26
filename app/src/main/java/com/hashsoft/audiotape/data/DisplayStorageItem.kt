package com.hashsoft.audiotape.data

/**
 * 表示用ストレージアイテム
 *
 * @param T StorageItemを継承したクラス
 * @property base ベースとなるストレージアイテム
 * @property index インデックス
 * @property color 色
 * @property icon アイコン
 * @property isResume レジューム再生か
 * @property contentPosition 再生位置
 */
data class DisplayStorageItem<T : StorageItem>(
    val base: T,
    val index: Int,
    val color: Int = 0,
    val icon: Int = 0,
    val isResume: Boolean = false,
    val contentPosition: Long = 0,
)
