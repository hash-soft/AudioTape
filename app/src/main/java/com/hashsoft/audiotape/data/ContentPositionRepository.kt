package com.hashsoft.audiotape.data

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * 再生コンテンツの現在位置を管理するリポジトリ
 */
class ContentPositionRepository {

    /**
     * 現在の再生位置を保持するMutableStateFlow
     * 外部からの変更は`update`メソッド経由でのみ許可される
     */
    var value: MutableStateFlow<Long?> = MutableStateFlow(null)
        private set

    /**
     * 再生位置を更新する
     * @param newPosition 新しい再生位置（ミリ秒）、または再生が停止している場合はnull
     */
    fun update(newPosition: Long?) {
        value.value = newPosition
    }
}
