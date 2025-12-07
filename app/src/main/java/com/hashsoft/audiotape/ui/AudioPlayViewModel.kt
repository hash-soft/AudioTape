package com.hashsoft.audiotape.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.AudioTapeSortOrder
import com.hashsoft.audiotape.data.ContentPositionRepository
import com.hashsoft.audiotape.data.ControllerStateRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.StorageItemListUseCase
import com.hashsoft.audiotape.data.StorageVolumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * オーディオ再生画面のViewModel
 *
 * @param _controller オーディオコントローラー
 * @param _playbackRepository 再生リポジトリ
 * @param _audioTapeRepository オーディオテープレポジトリ
 * @param _playingStateRepository 再生状態リポジトリ
 * @param storageItemListUseCase ストレージアイテムリストユースケース
 * @param _audioStoreRepository オーディオストアリポジトリ
 * @param _storageVolumeRepository ストレージボリュームリポジトリ
 * @param _contentPositionRepository コンテンツ位置リポジトリ
 */
@HiltViewModel
class AudioPlayViewModel @Inject constructor(
    private val _controller: AudioController,
    controllerStateRepository: ControllerStateRepository,
    private val _audioTapeRepository: AudioTapeRepository,
    private val _playingStateRepository: PlayingStateRepository,
    storageItemListUseCase: StorageItemListUseCase,
    private val _audioStoreRepository: AudioStoreRepository,
    private val _storageVolumeRepository: StorageVolumeRepository,
    private val _contentPositionRepository: ContentPositionRepository
) :
    ViewModel() {

    private val _playItemState = PlayItemState(
        controller = _controller,
        _audioTapeRepository,
        _audioStoreRepository,
        _storageVolumeRepository,
        _playingStateRepository,
        controllerStateRepository,
    )

    val displayPlayingState = _playItemState.displayPlayingState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val availableState = _playItemState.availableState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    /**
     * 現在の再生位置をFlowとして公開する。
     */
    val contentPosition = _contentPositionRepository.value.asStateFlow()

    /**
     * オーディオリスト内のメディアアイテムを設定する。
     * 指定されたインデックスのアイテムが現在再生中でなければ、コントローラーにメディアアイテムリストを設定する。
     *
     * @param index 再生を開始するアイテムのインデックス
     */
    fun setMediaItemsInAudioList(list: List<AudioItemDto>, index: Int, position: Long) {
        val audioItem = list.getOrNull(index) ?: return
        if (_controller.isCurrentById(audioItem.id)) {
            return
        }
        if (_controller.seekToById(audioItem.id, position)) {
            return
        }
        //_controller.setMediaItems(list, index, position)
    }

    /**
     * 現在の再生アイテムのパラメータ（リピート、音量、再生速度、ピッチ）をコントローラーに設定する。
     */
    fun setPlayingParameters(audioTape: AudioTapeDto) {
        _controller.setRepeat(audioTape.repeat)
        _controller.setVolume(audioTape.volume)
        _controller.setPlaybackParameters(audioTape.speed, audioTape.pitch)
    }

    /**
     * 現在のメディアを再生する。
     */
    fun play() = _controller.play()

    /**
     * 現在のメディアの再生を一時停止する。
     */
    fun pause() = _controller.pause()

    /**
     * 指定された位置にシークする。
     *
     * @param position シーク先の再生位置（ミリ秒）
     */
    fun seekTo(position: Long) {
        _contentPositionRepository.update(position)
        _controller.seekTo(position)
    }

    /**
     * 次の曲へ移動する
     */
    fun seekToNext() = _controller.seekToNext()

    /**
     * 前の曲へ移動する
     */
    fun seekToPrevious() = _controller.seekToPrevious()

    /**
     * 早送りする
     */
    fun seekForward() = _controller.seekForward()

    /**
     * 巻き戻しする
     */
    fun seekBack() = _controller.seekBack()

    /**
     * ソート順を更新する
     *
     * @param path パス
     * @param sortOrder ソート順
     */
    fun updateSortOrder(path: String, sortOrder: AudioTapeSortOrder) =
        viewModelScope.launch { _audioTapeRepository.updateSortOrder(path, sortOrder) }

    /**
     * リピートを設定する
     *
     * @param repeat リピートするかどうか
     */
    fun setRepeat(repeat: Boolean) = _controller.setRepeat(repeat)

    /**
     * リピートを更新する
     *
     * @param path パス
     * @param repeat リピートするかどうか
     */
    fun updateRepeat(path: String, repeat: Boolean) =
        viewModelScope.launch { _audioTapeRepository.updateRepeat(path, repeat) }

    /**
     * 音量を設定する
     *
     * @param volume 音量
     */
    fun setVolume(volume: Float) = _controller.setVolume(volume)

    /**
     * 音量を更新する
     *
     * @param path パス
     * @param volume 音量
     */
    fun updateVolume(path: String, volume: Float) =
        viewModelScope.launch { _audioTapeRepository.updateVolume(path, volume) }

    /**
     * 再生速度を設定する
     *
     * @param speed 再生速度
     */
    fun setSpeed(speed: Float) = _controller.setSpeed(speed)

    /**
     * 再生速度を更新する
     *
     * @param path パス
     * @param speed 再生速度
     */
    fun updateSpeed(path: String, speed: Float) =
        viewModelScope.launch { _audioTapeRepository.updateSpeed(path, speed) }

    /**
     * ピッチを設定する
     *
     * @param pitch ピッチ
     */
    fun setPitch(pitch: Float) = _controller.setPitch(pitch)

    /**
     * ピッチを更新する
     *
     * @param path パス
     * @param pitch ピッチ
     */
    fun updatePitch(path: String, pitch: Float) =
        viewModelScope.launch { _audioTapeRepository.updatePitch(path, pitch) }

}
