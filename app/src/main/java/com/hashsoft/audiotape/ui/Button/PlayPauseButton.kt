package com.hashsoft.audiotape.ui.Button

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 再生／一時停止ボタン
 *
 * @param isPlaying 再生中かどうか
 * @param enabled ボタンが有効かどうか
 * @param modifier モディファイア
 * @param onClick クリック時のコールバック
 */
@Composable
fun PlayPauseButton(
    isPlaying: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: (Boolean) -> Unit = {}
) {
    IconButton(
        onClick = {
            onClick(isPlaying)
        }, enabled = enabled
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            null,
            modifier = modifier
        )
    }
}
