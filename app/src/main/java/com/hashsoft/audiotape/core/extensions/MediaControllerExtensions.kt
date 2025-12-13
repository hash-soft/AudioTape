package com.hashsoft.audiotape.core.extensions

import android.os.Handler
import android.os.Looper
import androidx.media3.common.Player
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
                    Timber.d("#3 contentPosition = $contentPosition count = $mediaItemCount")
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
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> T.playingContentPositionFlow(
    flow: Flow<Boolean>,
    intervalMs: Long = 1000L
): Flow<Long> where T : Player =
    (flow.flatMapLatest { isPlaying ->
        if (isPlaying) contentPositionFlow(intervalMs) else {
            // Todo ここ怪しい 停止時にseekするだけで4回くる 確実に 0 ⇒ posになる
            // だがcontentPositionFlowのほうも普通に2回来ている
            Timber.d("#3 else position = $contentPosition, state = $isPlaying, count=${mediaItemCount}")
            val position = if (currentMediaItem == null) -1 else contentPosition
            flowOf(position)
        }
    })
