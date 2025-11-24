package com.hashsoft.audiotape.core.extensions

import android.os.Handler
import android.os.Looper
import androidx.media3.common.Player
import com.hashsoft.audiotape.data.ControllerState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber


/**
 * 指定された間隔でPlayerの現在の再生位置を放出するFlowを生成する。
 * @param intervalMs 再生位置を放出する間隔（ミリ秒）
 * @return 再生位置を放出するFlow
 */
fun <T> T.contentPositionFlow(intervalMs: Long = 1000L): Flow<Long> where T : Player =
    callbackFlow {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                try {
                    trySend(contentPosition)
                } catch (e: IllegalStateException) {
                    Timber.w(e)
                }
                handler.postDelayed(this, intervalMs)
            }
        }

        handler.post(runnable)
        awaitClose {
            handler.removeCallbacks(runnable)
        }

    }

/**
 * Playerが再生中の場合にのみ、指定された間隔で現在の再生位置を放出するFlowを生成する。
 * 再生が停止すると現在の再生位置、もしくは-1Lを放出する。
 * @param intervalMs 再生位置を放出する間隔（ミリ秒）
 * @return 再生位置を放出するFlow
 */
fun <T> T.playingContentPositionFlow(
    flow: Flow<ControllerState>,
    intervalMs: Long = 1000L
): Flow<Long> where T : Player =
    @OptIn(ExperimentalCoroutinesApi::class)
    (flow.flatMapLatest { state ->
        if (state.isPlaying) contentPositionFlow(intervalMs) else {
            val position = if (state.isReadyOk) contentPosition else -1L
            flowOf(position)
        }
    })
