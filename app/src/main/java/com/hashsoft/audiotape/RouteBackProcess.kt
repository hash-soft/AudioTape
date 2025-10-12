package com.hashsoft.audiotape


import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

/**
 * ルートの「戻る」処理をハンドリングする
 *
 * @param enabled 戻る処理を有効にするかどうか
 * @param onBackProcess 戻る処理が実行されたときに呼び出されるコールバック
 */
@Composable
fun RouteBackProcess(
    enabled: Boolean,
    onBackProcess: () -> Unit
) {
    BackHandler(enabled) {
        onBackProcess()
    }

}
