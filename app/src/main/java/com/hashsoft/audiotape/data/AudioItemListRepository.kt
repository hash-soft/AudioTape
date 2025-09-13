package com.hashsoft.audiotape.data

import android.os.Build
import com.hashsoft.audiotape.logic.StorageHelper

// ソートをどうするか
// 与えられたパスから取得するだけなのでcontextは不要
// 取得したデータから合計時間とか計算しておいて返す関数とか用意しておけばいいか
// どう再生serviceと関連するか
// まずは見栄えのための機能を作る]]

// １時振り分けしたオーディオファイルリストを取得
class AudioItemListRepository(private val _path: String) {

    fun getAudioItemList(): List<StorageItemDto> {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            StorageHelper.getFileList(_path).mapNotNull {
                if (it.isDirectory) {
                    return@mapNotNull null
                } else {
                    val isAudio = StorageHelper.isAudioExtension(it.path)
                    if (isAudio) {
                        StorageItemDto(
                            it.name,
                            it.path,
                            it.size,
                            it.lastModified,
                            StorageItemMetadata.UnanalyzedFile
                        )
                    } else {
                        return@mapNotNull null
                    }

                }
            }
        } else {
            StorageHelper.getFileList(_path).mapNotNull {
                if (it.isDirectory) {
                    return@mapNotNull null
                } else {
                    StorageItemDto(
                        it.name,
                        it.path,
                        it.size,
                        it.lastModified,
                        StorageItemMetadata.UnanalyzedFile
                    )
                }
            }
        }
    }
}
