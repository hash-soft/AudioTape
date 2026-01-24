package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow

/**
 * シンプルなオーディオアイテム
 *
 * @param name 名前
 * @param size サイズ
 * @param lastModified 最終更新日時
 * @param duration 長さ
 */
@Composable
fun SimpleAudioItem(
    name: String,
    size: Long,
    lastModified: Long,
    duration: Long
) {
    Column {
        Text(text = name, overflow = TextOverflow.Ellipsis, maxLines = 1)
        AudioFileSubInfoItem(
            size,
            lastModified,
            duration,
            false,
            0,
            MaterialTheme.typography.bodySmall
        )
    }
}
