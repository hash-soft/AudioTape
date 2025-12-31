package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hashsoft.audiotape.data.AudioTapeSortOrder
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme
import com.hashsoft.audiotape.ui.theme.currentItemBackgroundColor
import com.hashsoft.audiotape.ui.theme.currentItemContentColor
import com.hashsoft.audiotape.ui.theme.resolveColorForState
import com.hashsoft.audiotape.ui.theme.smallFontSize


/**
 * テープリストのアイテムを表示するComposable関数 (チェックボックス付き)
 *
 * @param index リスト内でのインデックス
 * @param title タイトル
 * @param folderPath フォルダパス
 * @param currentName 現在再生中のファイル名
 * @param position 再生位置
 * @param sortOrder ソート順
 * @param repeat リピート設定
 * @param speed 再生速度
 * @param volume 音量
 * @param pitch ピッチ
 * @param createTime 作成日時
 * @param lastPlayedAt 最終再生日時
 * @param isCurrent 現在再生中かどうか
 * @param state 状態
 * @param isChecked チェックされているかどうか
 * @param onCheckedChange チェック状態変更時のコールバック
 */
@Composable
fun TapeCheckItem(
    index: Int,
    title: String,
    folderPath: String,
    currentName: String,
    position: Long,
    sortOrder: AudioTapeSortOrder,
    repeat: Boolean,
    speed: Float,
    volume: Float,
    pitch: Float,
    createTime: Long,
    lastPlayedAt: Long,
    isCurrent: Boolean,
    state: Int,
    isChecked: Boolean,
    onCheckedChange: (checked: Boolean, index: Int) -> Unit = { _, _ -> },
) {
    Surface(
        contentColor = if (isCurrent) resolveColorForState(
            currentItemContentColor,
            state
        ) else resolveColorForState(MaterialTheme.colorScheme.onSurfaceVariant, state)
    ) {
        Row(
            modifier = Modifier
                .clickable { onCheckedChange(!isChecked, index) }
                .background(color = if (isCurrent) currentItemBackgroundColor else MaterialTheme.colorScheme.background),
        ) {
            Checkbox(
                checked = isChecked,
                modifier = Modifier.align(Alignment.CenterVertically),
                onCheckedChange = { onCheckedChange(it, index) }
            )
            Column(modifier = Modifier.weight(1.0f)) {
                Text(title)
                CurrentAudioNameItem(
                    index,
                    10,
                    position,
                    currentName,
                    LocalTextStyle.current.copy(fontSize = smallFontSize)
                )
                PlaybackValueItem(
                    volume,
                    speed,
                    pitch,
                    repeat,
                    LocalTextStyle.current.copy(fontSize = smallFontSize)
                )
                TapeTimeItem(
                    lastPlayedAt,
                    createTime,
                    LocalTextStyle.current.copy(fontSize = smallFontSize)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TapeCheckItemPreview() {
    AudioTapeTheme {
        TapeCheckItem(
            1,
            "directory",
            "path",
            "01 みえないつばさ",
            100,
            AudioTapeSortOrder.DATE_ASC,
            false,
            1.0f,
            1.0f,
            1.0f,
            1000,
            500,
            true,
            2,
            true
        )
    }
}
