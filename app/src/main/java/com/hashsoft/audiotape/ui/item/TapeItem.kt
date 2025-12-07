package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.combinedClickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hashsoft.audiotape.data.AudioTapeSortOrder
import com.hashsoft.audiotape.logic.TimeFormat.Companion.formatDateTimeHm
import com.hashsoft.audiotape.logic.TimeFormat.Companion.formatMillis
import com.hashsoft.audiotape.ui.AudioCallbackArgument


/**
 * テープリストのアイテムを表示するComposable関数
 *
 * @param index リスト内でのインデックス
 * @param folderPath フォルダパス
 * @param currentName 現在再生中のファイル名
 * @param position 再生位置
 * @param sortOrder ソート順
 * @param repeat リピート設定
 * @param speed 再生速度
 * @param volume 音量
 * @param pitch ピッチ
 * @param createTime 作成日時
 * @param updateTime 更新日時
 * @param color アイテムの背景色
 * @param audioCallback オーディオ関連のコールバック
 */
@Composable
fun TapeItem(
    index: Int,
    title:String,
    folderPath: String,
    currentName: String,
    position: Long,
    sortOrder: AudioTapeSortOrder,
    repeat: Boolean,
    speed: Float,
    volume: Float,
    pitch: Float,
    createTime: Long,
    updateTime: Long,
    color: Int,
    audioCallback: (AudioCallbackArgument) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = {
            Text(
                text = "${currentName}, position:${formatMillis(position)}, $sortOrder r:${repeat} v:${volume} p:${pitch} s:${speed}, create:${
                    formatDateTimeHm(
                        createTime
                    )
                }, update:${formatDateTimeHm(updateTime)}"
            )
        },
        trailingContent = {
            IconButton(
                onClick = { audioCallback(AudioCallbackArgument.TapeFolderOpen(folderPath)) }
            ) {
                Icon(
                    Icons.Default.FolderOpen,
                    null
                )
            }
        },
        modifier = Modifier.combinedClickable(onClick = {
            audioCallback(AudioCallbackArgument.TapeSelected(index))
        }, onLongClick = {
            audioCallback(AudioCallbackArgument.TapeSelected(index, true))
        }),
        colors = if (color > 0) ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant, // 背景色
            headlineColor = MaterialTheme.colorScheme.onSurfaceVariant         // 見出し文字色
        ) else {
            ListItemDefaults.colors()
        }
    )
}
