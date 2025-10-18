package com.hashsoft.audiotape.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.PlaybackRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.ResumeAudioRepository
import com.hashsoft.audiotape.data.StorageItemListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

/**
 * オーディオ再生画面のViewModel
 *
 * @param _controller オーディオコントローラー
 * @param playbackRepository 再生リポジトリ
 * @param _audioTapeRepository オーディオテープレポジトリ
 * @param playingStateRepository 再生状態リポジトリ
 * @param resumeAudioRepository レジュームオーディオリポジトリ
 * @param storageItemListRepository ストレージアイテムリストリポジトリ
 */
@HiltViewModel
class AudioPlayViewModel @Inject constructor(
    private val _controller: AudioController,
    playbackRepository: PlaybackRepository,
    private val _audioTapeRepository: AudioTapeRepository,
    playingStateRepository: PlayingStateRepository,
    resumeAudioRepository: ResumeAudioRepository,
    storageItemListUseCase: StorageItemListUseCase
) :
    ViewModel() {

    val playItemState = PlayItemState(
        playbackRepository,
        _audioTapeRepository,
        resumeAudioRepository
    )

    val playListState = PlayListState(
        storageItemListUseCase
    )


    init {
        viewModelScope.launch {
            @OptIn(ExperimentalCoroutinesApi::class)
            playingStateRepository.playingStateFlow().flatMapLatest { state ->
                playListState.loadStorageCache(state.folderPath)
                combine(
                    _audioTapeRepository.findByPath(state.folderPath),
                    playbackRepository.data
                ) { audioTape, playback ->
                    audioTape to playback
                }
            }.collect { (audioTape, playback) ->
                playListState.updateList(audioTape)
                playItemState.updatePlayAudioForExclusive(audioTape, playback)
            }
        }
    }

    /**
     * 再生を開始する
     */
    fun play() = _controller.play()

    /**
     * 再生を一時停止する
     */
    fun pause() = _controller.pause()

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


}
