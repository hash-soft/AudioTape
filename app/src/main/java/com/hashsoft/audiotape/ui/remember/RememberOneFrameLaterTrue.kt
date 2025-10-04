package com.hashsoft.audiotape.ui.remember

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.hashsoft.audiotape.logic.RefValue

/**
 * コンポーズの最初のフレームの描画が終わった後、valueがtrueになるRefValueを返す。
 * LaunchedEffectが1フレーム遅れることを利用する。
 *
 * @return 最初のフレームの描画が終わった後、valueがtrueになるRefValue<Boolean>
 */
@Composable
fun rememberOneFrameLaterTrue(): RefValue<Boolean> {
    val flag = remember { RefValue(false) }
    LaunchedEffect(Unit) {
        flag.value = true
    }
    return flag
}
