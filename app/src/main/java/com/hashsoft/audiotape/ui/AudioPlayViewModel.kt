package com.hashsoft.audiotape.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.AudioTapeSortOrder
import com.hashsoft.audiotape.data.PlaybackDto
import com.hashsoft.audiotape.data.PlaybackRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.StorageItemListUseCase
import com.hashsoft.audiotape.data.StorageVolumeRepository
import com.hashsoft.audiotape.data.VolumeItem
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
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
 */
@HiltViewModel
class AudioPlayViewModel @Inject constructor(
    private val _controller: AudioController,
    private val _playbackRepository: PlaybackRepository,
    private val _audioTapeRepository: AudioTapeRepository,
    private val _playingStateRepository: PlayingStateRepository,
    storageItemListUseCase: StorageItemListUseCase,
    private val _audioStoreRepository: AudioStoreRepository,
    private val _storageVolumeRepository: StorageVolumeRepository
) :
    ViewModel() {

    /**
     * 再生アイテムの状態
     */
    val playItemState = PlayItemState(
        _playbackRepository,
        _audioTapeRepository,
        _audioStoreRepository
    )

    /**
     * 再生リストの状態
     */
    val playListState = PlayListState(
        storageItemListUseCase
    )


    /**
     * 初期化時にボリュームの変更監視を開始する。
     * ボリューム変更、オーディオストア、再生状態の変更を検知し、
     * 再生アイテムの状態を更新するフローを構築する。
     */
    init {
        viewModelScope.launch {
            @OptIn(ExperimentalCoroutinesApi::class)
            _storageVolumeRepository.volumeChangeFlow().flatMapLatest { volumes ->
                watchAudioStore(volumes)
            }.collect { (volumes, audioTape, playback) ->
                playItemState.updatePlayAudioForExclusive(volumes, audioTape, playback)
            }
        }
    }

    /**
     * オーディオストアの変更を監視する
     *
     * @param volumes ボリュームリスト
     * @return ボリュームリスト、オーディオテープ、再生状態のTripleを返すFlow
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun watchAudioStore(volumes: List<VolumeItem>): Flow<Triple<List<VolumeItem>, AudioTapeDto, PlaybackDto>> {
        // 待つだけ
        return _audioStoreRepository.updateFlow.flatMapLatest { watchPlayingState(volumes) }
    }

    /**
     * 再生状態の変更を監視する
     *
     * @param volumes ボリュームリスト
     * @return ボリュームリスト、オーディオテープ、再生状態のTripleを返すFlow
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun watchPlayingState(volumes: List<VolumeItem>): Flow<Triple<List<VolumeItem>, AudioTapeDto, PlaybackDto>> {
        return _playingStateRepository.playingStateFlow().flatMapLatest { state ->
            val sortOrder = _audioTapeRepository.findSortOrderByPath(state.folderPath).first()
            playListState.updateList(volumes, state.folderPath, sortOrder)
            // Todo controllerのmediaItemと合わせも必要 作成されている場合のみ
            // ここでやるのではなくフラグとcontrollerの状態を組み合わせて別で監視したほうがよさそう
            combine(
                _audioTapeRepository.findByPath(state.folderPath),
                _playbackRepository.data
            ) { audioTape, playback ->
                Triple(volumes, audioTape, playback)
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
     * リストをソートする
     *
     * @param sortOrder ソート順
     */
    fun sortList(sortOrder: AudioTapeSortOrder) {
        playListState.sortList(sortOrder)
        _controller.sortMediaItems(playListState.list.value)
    }

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
