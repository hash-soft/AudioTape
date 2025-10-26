package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.hashsoft.audiotape.ui.text.AudioDurationText
import com.hashsoft.audiotape.ui.text.SizeAndLastModifiedText

/**
 * オーディオファイルのサブ情報アイテム
 *
 * @param size サイズ
 * @param lastModified 最終更新日時
 * @param duration 長さ
 * @param isResume レジューム再生か
 * @param contentPosition 再生位置
 * @param style テキストスタイル
 */
@Composable
fun AudioFileSubInfoItem(
    size: Long,
    lastModified: Long,
    duration: Long,
    isResume: Boolean,
    contentPosition: Long,
    style: TextStyle = LocalTextStyle.current
) {
    if (isResume) {
        AudioFileSubInfoItemTwoRow(size, lastModified, duration, contentPosition, style)
    } else {
        AudioFileSubInfoItemOneRow(size, lastModified, duration, style)
    }
}

/**
 * オーディオファイルのサブ情報アイテム（1行表示）
 *
 * @param size サイズ
 * @param lastModified 最終更新日時
 * @param duration 長さ
 * @param style テキストスタイル
 */
@Composable
private fun AudioFileSubInfoItemOneRow(
    size: Long,
    lastModified: Long,
    duration: Long,
    style: TextStyle = LocalTextStyle.current
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        AudioDurationText(duration, modifier = Modifier.weight(1f), style)
        SizeAndLastModifiedText(size, lastModified, style = style)
    }
}

/**
 * オーディオファイルのサブ情報アイテム（2行表示）
 *
 * @param size サイズ
 * @param lastModified 最終更新日時
 * @param duration 長さ
 * @param contentPosition 再生位置
 * @param style テキストスタイル
 */
@Composable
private fun AudioFileSubInfoItemTwoRow(
    size: Long,
    lastModified: Long,
    duration: Long,
    contentPosition: Long,
    style: TextStyle = LocalTextStyle.current
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            AudioDurationText(duration, modifier = Modifier.weight(1f), style)
            SizeAndLastModifiedText(size, lastModified, style = style)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AudioDurationText(contentPosition)
            LinearProgressIndicator(
                progress = { contentPosition.toFloat() / duration },
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minWidth = 0.dp)
                    .fillMaxWidth()
                    .padding(start = 10.dp),
                gapSize = 0.dp,
                drawStopIndicator = {}
            )
        }
    }
}
