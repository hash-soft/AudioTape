package com.hashsoft.audiotape.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.AudioTapeSortOrder
import com.hashsoft.audiotape.data.ContentPositionRepository
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.io.File

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
    private val _playbackRepository: PlaybackRepository,
    private val _audioTapeRepository: AudioTapeRepository,
    private val _playingStateRepository: PlayingStateRepository,
    storageItemListUseCase: StorageItemListUseCase,
    private val _audioStoreRepository: AudioStoreRepository,
    private val _storageVolumeRepository: StorageVolumeRepository,
    private val _contentPositionRepository: ContentPositionRepository
) :
    ViewModel() {

    /**
     * 現在再生中のアイテムに関する状態を管理する。
     */
    val playItemState = PlayItemState(
        _playbackRepository,
        _audioTapeRepository,
        _audioStoreRepository
    )

    /**
     * 再生リストに関する状態を管理する。
     */
    val playListState = PlayListState(
        storageItemListUseCase
    )

    /**
     * 現在の再生位置をFlowとして公開する。
     */
    val contentPosition = _contentPositionRepository.value.asStateFlow()

    /**
     * ViewModelの初期化処理。
     * ストレージボリュームの変更を監視し、変更があった場合にオーディオストアと再生状態を監視するフローを開始する。
     * これにより、再生アイテムの状態が動的に更新される。
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
     * 指定されたボリュームリストに基づいてオーディオストアの変更を監視する。
     *
     * @param volumes 監視対象のボリュームリスト
     * @return ボリュームリスト、[AudioTapeDto]、[PlaybackDto]のTripleを含むFlow
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun watchAudioStore(volumes: List<VolumeItem>): Flow<Triple<List<VolumeItem>, AudioTapeDto, PlaybackDto>> {
        // updateFlowが更新されるのを待ってから次の処理へ進む
        return _audioStoreRepository.updateFlow.flatMapLatest { watchPlayingState(volumes) }
    }

    /**
     * 指定されたボリュームリストに基づいて再生状態の変更を監視する。
     *
     * @param volumes 監視対象のボリュームリスト
     * @return ボリュームリスト、[AudioTapeDto]、[PlaybackDto]のTripleを含むFlow
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun watchPlayingState(volumes: List<VolumeItem>): Flow<Triple<List<VolumeItem>, AudioTapeDto, PlaybackDto>> {
        return _playingStateRepository.playingStateFlow().flatMapLatest { state ->
            val sortOrder = _audioTapeRepository.findSortOrderByPath(state.folderPath).first()
            playListState.updateList(volumes, state.folderPath, sortOrder)
            // MediaItemをlistと合わせる
            _controller.replaceMediaItemsWith(playListState.list.value)
            combine(
                _audioTapeRepository.findByPath(state.folderPath),
                _playbackRepository.data
            ) { audioTape, playback ->
                Triple(volumes, audioTape, playback)
            }
        }
    }

    /**
     * オーディオリスト内のメディアアイテムを設定する。
     * 指定されたインデックスのアイテムが現在再生中でなければ、コントローラーにメディアアイテムリストを設定する。
     *
     * @param index 再生を開始するアイテムのインデックス
     */
    fun setMediaItemsInAudioList(index: Int = 0) {
        val audioList = playListState.list.value
        val audioItem = audioList[index]
        if (_controller.isCurrentById(audioItem.id)) {
            return
        }
        val playAudio = playItemState.item.value ?: return
        val file = File(playAudio.path)
        val position = if (audioItem.name == file.name) playAudio.contentPosition else 0
        if (_controller.seekToById(audioItem.id, position)) {
            return
        }
        _controller.setMediaItems(audioList, index, position)
    }

    /**
     * 現在の再生アイテムのパラメータ（リピート、音量、再生速度、ピッチ）をコントローラーに設定する。
     */
    fun setPlayingParameters() {
        playItemState.item.value?.audioTape?.let {
            _controller.setRepeat(it.repeat)
            _controller.setVolume(it.volume)
            _controller.setPlaybackParameters(it.speed, it.pitch)
        }
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
        if (_controller.isCurrentMediaItem()) {
            _controller.seekTo(position)
        } else {
            playItemState.updatePlaybackPosition(position)
        }
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
