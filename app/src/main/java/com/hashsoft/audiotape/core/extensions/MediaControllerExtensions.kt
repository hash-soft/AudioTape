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
 * Playerの再生状態の変更を監視するFlowを生成する。
 * 再生中は`true`、停止中は`false`を放出する。
 * @return 再生状態を放出するFlow
 */
fun <T> T.isPlayingFlow(): Flow<Boolean> where T : Player = callbackFlow {
    val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            trySend(isPlaying)
        }
    }
    addListener(listener)
    awaitClose { removeListener(listener) }
}

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
                    Timber.e(e)
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
 * 再生が停止すると`null`を放出する。
 * @param intervalMs 再生位置を放出する間隔（ミリ秒）
 * @return 再生位置またはnullを放出するFlow
 */
fun <T> T.playingContentPositionFlow(intervalMs: Long = 1000L): Flow<Long> where T : Player =
    @OptIn(ExperimentalCoroutinesApi::class)
    isPlayingFlow().flatMapLatest { isPlaying ->
        if (isPlaying) contentPositionFlow(intervalMs) else flowOf(-1)
    }
