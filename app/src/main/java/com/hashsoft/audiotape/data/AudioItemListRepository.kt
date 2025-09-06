package com.hashsoft.audiotape.data

import com.hashsoft.audiotape.logic.StorageHelper

// ソートをどうするか
// 与えられたパスから取得するだけなのでcontextは不要
// 取得したデータから合計時間とか計算しておいて返す関数とか用意しておけばいいか
// どう再生serviceと関連するか
// まずは見栄えのための機能を作る]]

// ここはオーディオ再生専用画面の時に使うやつ
class AudioItemListRepository(private val _path: String) {

    fun getAudioItemList(): List<AudioItemDto> {
        //val retriever = MediaMetadataRetriever()
        //val checker = AudioFileChecker()
        val result = StorageHelper.getFileList(_path).mapNotNull {
            if (it.isDirectory) {
                return@mapNotNull null
            } else {
                AudioItemDto(
                    it.name,
                    it.path,
                    it.size,
                    it.lastModified,
                    AudioItemMetadata("", "", "", 0L)
                )
                //val result = checker.getMetadata(retriever, it.path)
//                result.fold(onSuccess = { metadata ->
//                    AudioItemDto(it.name, it.path, it.size, it.lastModified, metadata)
//                }, onFailure = {
//                    return@mapNotNull null
//                })
//            }
            }
        }
        //retriever.release()
        return result
    }
}