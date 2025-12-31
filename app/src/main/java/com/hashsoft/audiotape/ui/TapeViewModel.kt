package com.hashsoft.audiotape.ui

import androidx.collection.IntSet
import androidx.collection.intSetOf
import androidx.collection.mutableIntSetOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.FolderStateRepository
import com.hashsoft.audiotape.data.LibraryStateRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.StorageVolumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File


/**
 * オーディオテープ（再生フォルダ）の一覧表示および操作を管理するViewModel
 */
@HiltViewModel
class TapeViewModel @Inject constructor(
    private val _controller: AudioController,
    audioTapeRepository: AudioTapeRepository,
    private val _playingStateRepository: PlayingStateRepository,
    private val _audioTapeRepository: AudioTapeRepository,
    private val _folderStateRepository: FolderStateRepository,
    private val _audioStoreRepository: AudioStoreRepository,
    storageVolumeRepository: StorageVolumeRepository,
    libraryStateRepository: LibraryStateRepository
) :
    ViewModel() {

    /**
     * ソート順やボリュームの変更を監視し、テープ情報とディレクトリ構造のリストを生成する内部State
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _baseState =
        libraryStateRepository.tapeListSortOrderFlow().flatMapLatest { sortOrder ->
            combine(
                storageVolumeRepository.volumeChangeFlow(),
                audioTapeRepository.getAll(sortOrder)
            ) { volumes, list ->
                list.map { audioTape ->
                    val treeList =
                        AudioStoreRepository.pathToTreeList(volumes, audioTape.folderPath)
                    audioTape to treeList
                }
            }
        }

    /**
     * 画面表示用のテープリスト
     * 現在再生中のフォルダかどうかの情報を含めて公開される
     */
    val displayTapeListState =
        combine(_baseState, _playingStateRepository.playingStateFlow()) { list, playingState ->
            list.map {
                val audioTape = it.first
                val isCurrent = playingState.folderPath == audioTape.folderPath
                DisplayTapeItem(audioTape, it.second, isCurrent)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    /**
     * 削除対象として選択されたテープのIDセット
     */
    private val _deleteIdsSet = MutableStateFlow(intSetOf())

    /**
     * 削除対象として選択されたテープのIDセットの公開プロパティ
     */
    val deleteIdsSet = _deleteIdsSet.asStateFlow()

    /**
     * 再生するフォルダを切り替える
     * 現在再生中のフォルダと同じ場合は何もしない
     * 切り替え前に現在の再生位置を保存し、新しいフォルダの情報をレポジトリに保存する
     *
     * @param tape 切り替え先のテープ情報
     */
    fun switchPlayingFolder(tape: AudioTapeDto) {
        val file = _controller.getCurrentMediaItemUri()?.run {
            File(_audioStoreRepository.uriToPath(this))
        }
        if (file?.parent == tape.folderPath) {
            // 同じテープの場合は継続
            return
        }
        val prevPosition = _controller.getCurrentPosition()
        _controller.clearMediaItems()
        viewModelScope.launch {
            _playingStateRepository.saveFolderPath(tape.folderPath)
            if (file != null) {
                _audioTapeRepository.updatePlayingPosition(
                    file.parent ?: "",
                    file.name,
                    prevPosition,
                    false
                )
            }
        }
    }


    /**
     * 再生・一時停止の状態を設定する
     *
     * @param playWhenReady trueで再生、falseで一時停止
     */
    fun playWhenReady(playWhenReady: Boolean) = _controller.playWhenReady(playWhenReady)

    /**
     * テープ固有の再生パラメータ（リピート設定、音量、再生速度、ピッチ）を適用する
     *
     * @param audioTape 適用する設定を持つテープ情報
     */
    fun setPlayingParameters(audioTape: AudioTapeDto) {
        _controller.setRepeat(audioTape.repeat)
        _controller.setVolume(audioTape.volume)
        _controller.setPlaybackParameters(audioTape.speed, audioTape.pitch)
    }

    /**
     * 選択されたフォルダパスを保存する
     *
     * @param path 保存するパス
     */
    fun saveSelectedPath(path: String) = viewModelScope.launch {
        _folderStateRepository.saveSelectedPath(path)
    }

    /**
     * 削除対象のリストにIDを追加する
     *
     * @param id 追加するテープのインデックスID
     */
    fun addDeleteId(id: Int) {
        _deleteIdsSet.update { currentSet ->
            mutableIntSetOf().apply {
                addAll(currentSet)
                add(id)
            }
        }
    }

    /**
     * 削除対象のリストからIDを削除する
     *
     * @param id 削除するテープのインデックスID
     */
    fun removeDeleteId(id: Int) {
        _deleteIdsSet.update { currentSet ->
            mutableIntSetOf().apply {
                addAll(currentSet)
                remove(id)
            }
        }
    }

    /**
     * 削除対象のIDリストをリセットする
     */
    fun resetDeleteIds() {
        _deleteIdsSet.update { intSetOf() }
    }

    /**
     * 削除対象のIDリストを一括設定する
     *
     * @param ids 設定するIDセット
     */
    fun setDeletedIds(ids: IntSet) {
        _deleteIdsSet.update { ids }
    }

    /**
     * 選択されているテープを削除する
     * 再生中のテープが削除対象に含まれる場合、再生を停止して状態をクリアする
     *
     * @param onDeletedAfter 削除処理完了後に実行されるコールバック
     */
    fun deleteSelectedTape(onDeletedAfter: () -> Unit) = viewModelScope.launch {
        val list: MutableList<AudioTapeDto> = mutableListOf()
        _deleteIdsSet.value.forEach { id ->
            val tape = displayTapeListState.value.getOrNull(id) ?: return@forEach
            list.add(tape.audioTape)
        }
        // 削除対象のテープが演奏設定に場合は演奏設定を外し、MediaItemを空にする
        val target = _playingStateRepository.getPlayingState().folderPath
        val tape = list.find { it.folderPath == target }
        if (tape != null) {
            _controller.clearMediaItems()
            _playingStateRepository.saveFolderPath("")
        }
        _audioTapeRepository.deleteTapes(list)
        _deleteIdsSet.update { intSetOf() }
        onDeletedAfter()
    }
}

/**
 * 画面表示用のテープ情報
 *
 * @property audioTape テープの基本情報
 * @property treeList フォルダのツリー構造リスト
 * @property isCurrent 現在再生中のテープかどうか
 */
data class DisplayTapeItem(
    val audioTape: AudioTapeDto = AudioTapeDto("", ""),
    val treeList: List<String>? = null,
    val isCurrent: Boolean = false
)
