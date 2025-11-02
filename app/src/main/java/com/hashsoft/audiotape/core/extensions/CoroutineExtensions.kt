package com.hashsoft.audiotape.core.extensions

import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.google.common.util.concurrent.Uninterruptibles
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.ExecutionException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * ListenableFutureを中断可能なコルーチンで待機可能にする拡張関数。
 *
 * @return Futureの結果
 * @throws java.util.concurrent.CancellationException Futureがキャンセルされた場合
 * @throws ExecutionException Futureが例外で完了した場合
 */
suspend fun <T> ListenableFuture<T>.await(): T {
    try {
        if (isDone) return Uninterruptibles.getUninterruptibly(this)
    } catch (e: ExecutionException) {
        // ExecutionExceptionは、取得したFutureからスローされうる唯一の例外です
        // (CancellationExceptionを除く)。キャンセルは上位に伝播され、
        // この中断関数を実行しているコルーチンがそれを処理できるようにします。
        // ここで他の例外が発生した場合、それはFuture実装の非常に基本的なバグを示します。
        throw e.nonNullCause()
    }

    return suspendCancellableCoroutine { cont: CancellableContinuation<T> ->
        addListener(
            ToContinuation(this, cont),
            MoreExecutors.directExecutor()
        )
        cont.invokeOnCancellation {
            cancel(false)
        }
    }
}

/**
 * ExecutionExceptionからnullでない原因を取得する。
 *
 * @return nullでないThrowable
 */
private fun ExecutionException.nonNullCause(): Throwable {
    return this.cause!!
}

/**
 * ListenableFutureの結果をCancellableContinuationに変換するためのRunnable。
 *
 * @param T 型
 * @property futureToObserve 監視対象のListenableFuture
 * @property continuation 継続
 */
private class ToContinuation<T>(
    val futureToObserve: ListenableFuture<T>,
    val continuation: CancellableContinuation<T>
) : Runnable {
    override fun run() {
        if (futureToObserve.isCancelled) {
            continuation.cancel()
        } else {
            try {
                continuation.resume(Uninterruptibles.getUninterruptibly(futureToObserve))
            } catch (e: ExecutionException) {
                // ExecutionExceptionは、取得したFutureからスローされうる唯一の例外です。
                // ここで他の例外が発生した場合、それはFuture実装の非常に基本的なバグを示します。
                continuation.resumeWithException(e.nonNullCause())
            }
        }
    }
}
