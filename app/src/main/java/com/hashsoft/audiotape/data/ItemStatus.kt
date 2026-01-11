package com.hashsoft.audiotape.data

enum class ItemStatus {
    Normal,        // 正常
    Warning,       // 異常だけど再生するための選択はできる（赤み）事前にわかる異常は表示しないので実質使われていない
    Disabled,      // 再生可能項目なし（グレーアウト）
    Missing        // 存在しない（赤み ＋ グレーアウト）
}