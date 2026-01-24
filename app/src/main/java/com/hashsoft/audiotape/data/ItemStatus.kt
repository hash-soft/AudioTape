package com.hashsoft.audiotape.data

enum class ItemStatus {
    Normal,        // 正常
    Warning,       // 異常だけど再生するための選択はできる（赤み）
    Disabled,      // 再生可能項目なし（グレーアウト）
    Missing;        // 存在しない（赤み ＋ グレーアウト）

    companion object {
        fun isPlayable(status: ItemStatus): Boolean {
            return status == Normal || status == Warning
        }

        fun isSkippable(status: ItemStatus) = isPlayable(status)

        fun isSeekable(status: ItemStatus): Boolean {
            return status == Normal
        }
    }
}