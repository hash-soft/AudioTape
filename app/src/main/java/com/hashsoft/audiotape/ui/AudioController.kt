package com.hashsoft.audiotape.ui

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.PlaybackParameters
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.hashsoft.audiotape.core.extensions.await
import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.logic.MediaItemHelper
import com.hashsoft.audiotape.service.PlaybackService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber


/**
 * オーディオ再生を制御するコントローラー
 * Media3 MediaControllerのラッパー
 */
class AudioController(
    private val _availableStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(
        false
    )
) {

    private var _controllerFuture: ListenableFuture<MediaController>? = null
    private var _controller: MediaController? = null

    val availableStateFlow = _availableStateFlow.asStateFlow()

    /**
     * コントローラーをビルドする
     *
     * @param context コンテキスト
     */
    suspend fun buildController(context: Context) {
        val sessionToken =
            SessionToken(context, ComponentName(context, PlaybackService::class.java))
        _controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        try {
            _controller = _controllerFuture?.await()
            Timber.d("#1 Controller connected")
            _availableStateFlow.update { true }
        } catch (e: Exception) {
            Timber.w(e, "#1 Failed to connect to MediaController")
            releaseController()
        }
    }

    /**
     * コントローラーを解放する
     */
    fun releaseController() {
        Timber.d("#1 Controller disconnected")
        _availableStateFlow.update { false }
        _controller?.let {
            it.release()
            _controller = null
        }
        _controllerFuture?.let {
            MediaController.releaseFuture(it)
            _controllerFuture = null
        }
    }

    /**
     * 現在のメディアアイテムが存在するかどうか
     *
     * @return 存在する場合true
     */
    fun isCurrentMediaItem(): Boolean {
        return _controller?.currentMediaItem != null
    }

    fun playWhenReady(playWhenReady: Boolean) = _controller?.playWhenReady = playWhenReady


    fun prepare() = _controller?.prepare()

    /**
     * 再生
     */
    fun play() = _controller?.play()


    /**
     * 一時停止
     */
    fun pause() = _controller?.pause()


    /**
     * 再生パラメータを設定する
     *
     * @param speed 再生速度
     * @param pitch ピッチ
     */
    fun setPlaybackParameters(speed: Float, pitch: Float) {
        _controller?.playbackParameters = PlaybackParameters(speed, pitch)
    }

    /**
     * 再生速度を設定する
     *
     * @param speed 再生速度
     */
    fun setSpeed(speed: Float) {
        _controller?.run {
            playbackParameters = PlaybackParameters(speed, playbackParameters.pitch)
        }
    }

    /**
     * ピッチを設定する
     *
     * @param pitch ピッチ
     */
    fun setPitch(pitch: Float) {
        _controller?.run {
            playbackParameters = PlaybackParameters(playbackParameters.speed, pitch)
        }
    }

    /**
     * 音量を設定する
     *
     * @param volume 音量
     */
    fun setVolume(volume: Float) {
        _controller?.volume = volume
    }

    /**
     * リピートモードを設定する
     *
     * @param repeat リピートする場合true
     */
    fun setRepeat(repeat: Boolean) {
        _controller?.repeatMode =
            if (repeat) MediaController.REPEAT_MODE_ALL else MediaController.REPEAT_MODE_OFF
    }

    fun getCurrentMediaItemUri() = _controller?.currentMediaItem?.localConfiguration?.uri

    /**
     * 現在の再生位置を取得する
     *
     * @return 再生位置(ms)
     */
    fun getCurrentPosition(): Long = _controller?.currentPosition ?: -1L

    fun getContentPosition(): Long = _controller?.contentPosition ?: -1L

    /**
     * 指定した位置にシークする
     *
     * @param position シークする位置(ms)
     */
    fun seekTo(position: Long) {
        if (position < 0) {
            Timber.e("seekTo position is negative $position")
            return
        }
        _controller?.seekTo(position)
    }

    /**
     * 次のメディアアイテムにシークする
     */
    fun seekToNext() {
        // リピートじゃない場合最後だと先頭に戻らないので無理やり先頭に戻している
        _controller?.run {
            if (mediaItemCount == 0) {
                return@run
            }
            if (nextMediaItemIndex < 0) {
                seekTo(0, 0)
            } else {
                seekToNext()
            }
        }
    }

    /**
     * 前のメディアアイテムにシークする
     */
    fun seekToPrevious() {
        // リピートじゃない場合最初だと最後に戻らないので無理やり最後に戻している
        // previousMediaItemIndexだけで判断すると途中の位置でも最後に戻ってしまうので2秒以内の再生位置なら最後に戻す
        _controller?.run {
            if (mediaItemCount == 0) {
                return@run
            }
            if (previousMediaItemIndex < 0) {
                if (currentPosition < 2000) {
                    seekTo(mediaItemCount - 1, 0)
                } else {
                    seekToPrevious()
                }
            } else {
                seekToPrevious()
            }
        }
    }

    /**
     * 早送りする
     */
    fun seekForward() {
        _controller?.run {
            seekForward()
        }
    }

    /**
     * 巻き戻しする
     */
    fun seekBack() {
        _controller?.run {
            seekBack()
        }
    }

    fun getMediaItemCount() = _controller?.mediaItemCount ?: -1

    fun setMediaItems(list: List<AudioItemDto>, indexName: String, positionMs: Long = 0) {
        val startIndex = list.indexOfFirst { it.name == indexName }
        setMediaItems(list, if (startIndex < 0) 0 else startIndex, positionMs)
    }


    /**
     * メディアアイテムのリストを設定する
     *
     * @param audioList オーディオリスト
     * @param mediaItemIndex 開始するメディアアイテムのインデックス
     * @param positionMs 開始位置(ms)
     */
    fun setMediaItems(
        audioList: List<AudioItemDto>,
        mediaItemIndex: Int = 0,
        positionMs: Long = 0
    ) {
        if (audioList.isEmpty()) {
            return
        }
        val mediaItems = audioList.map { audio -> MediaItemHelper.audioItemToMediaItem(audio) }

        // 初期位置も合わせて設定する
        _controller?.run {
            setMediaItems(mediaItems, mediaItemIndex, positionMs)
        }
    }

    /**
     * 指定されたIDが現在のメディアアイテムと一致するかどうか
     *
     * @param id 確認するID
     * @return 一致する場合true
     */
    fun isCurrentById(id: Long): Boolean {
        return _controller?.run {
            val currentId = currentMediaItem?.mediaId ?: ""
            currentId == id.toString()
        } ?: false
    }

    /**
     * 指定されたIDのメディアアイテムにシークする
     *
     * @param id シークするメディアアイテムのID
     * @param position シークする位置(ms)
     * @return 成功した場合true
     */
    fun seekToById(id: Long, position: Long): Boolean {
        return _controller?.run {
            for (i in 0 until mediaItemCount) {
                val item = getMediaItemAt(i)
                if (item.mediaId == id.toString()) {
                    seekTo(i, position)
                    return@run true
                }
            }
            false
        } ?: false
    }

    /**
     * メディアアイテムのリストを並べ替える
     *
     * @param list 並べ替え後のオーディオリスト
     */
    fun sortMediaItems(list: List<AudioItemDto>) {
        _controller?.run {
            for (i in 0 until mediaItemCount) {
                val item = getMediaItemAt(i)
                // audioItemsから同じidのitemのindexを探す
                val index = list.indexOfFirst { audioItem ->
                    audioItem.id.toString() == item.mediaId
                }
                if (index == -1) {
                    continue
                }
                // indexが違ってたらその場所に移動
                if (index != i) {
                    moveMediaItem(i, index)
                }
            }
        }
    }

    fun clearMediaItems() = _controller?.clearMediaItems()

    fun replaceMediaItemsWith(list: List<AudioItemDto>) {
        val player = _controller ?: return
        if (list.isEmpty()) {
            player.clearMediaItems()
            return
        }
        if (player.mediaItemCount == 0) {
            return
        }

        // 追加
        list.forEach {
            for (i in 0 until player.mediaItemCount) {
                val item = player.getMediaItemAt(i)
                if (item.mediaId == it.id.toString()) {
                    return@forEach
                }
            }
            player.addMediaItem(player.mediaItemCount, MediaItemHelper.audioItemToMediaItem(it))
        }
        // 削除
        for (i in player.mediaItemCount - 1 downTo 0) {
            val item = player.getMediaItemAt(i)
            list.find { it.id.toString() == item.mediaId } ?: player.removeMediaItem(i)
        }
        // ソート
        sortMediaItems(list)

    }

}
