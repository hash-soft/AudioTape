package com.hashsoft.audiotape.data


/**
 * ストレージボリュームのアイテムを表します。
 *
 * @property name ボリューム名
 * @property path ボリュームへのパス
 * @property lastModified 最終更新日時
 * @property mediaStorageVolumeName メディアストレージのボリューム名
 */
data class VolumeItem(
    val name: String,
    val path: String,
    val lastModified: Long,
    val mediaStorageVolumeName: String
)
