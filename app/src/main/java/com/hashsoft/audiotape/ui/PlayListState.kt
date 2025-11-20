package com.hashsoft.audiotape.ui

import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.data.AudioTapeSortOrder
import com.hashsoft.audiotape.data.StorageItemListUseCase
import com.hashsoft.audiotape.data.VolumeItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * 再生リストの状態を管理するクラス
 *
 * @param _storageItemListUseCase ストレージからアイテムリストを取得するためのユースケース
 * @param _list オーディオアイテムのリストを保持するMutableStateFlow
 */
class PlayListState(
    private val _storageItemListUseCase: StorageItemListUseCase,
    private val _list: MutableStateFlow<List<AudioItemDto>> = MutableStateFlow(
        emptyList()
    ),
) {
    /**
     * オーディオアイテムのリスト
     */
    val list: StateFlow<List<AudioItemDto>> = _list.asStateFlow()

    /**
     * 指定されたパスとソート順に基づいてリストを更新する
     *
     * @param volumes ボリュームのリスト
     * @param path フォルダのパス
     * @param sortOrder ソート順
     */
    fun updateList(volumes: List<VolumeItem>, path: String, sortOrder: AudioTapeSortOrder?) {
        val list = _storageItemListUseCase.getAudioItemList(
            volumes, path, sortOrder ?: AudioTapeSortOrder.NAME_ASC
        )
        _list.update { list }
    }


    /**
     * 現在のリストをソートする
     *
     * @param sortOder ソート順
     */
    fun sortList(sortOder: AudioTapeSortOrder) {
        val list = StorageItemListUseCase.sortedAudioList(_list.value, sortOder)
        _list.update { list }
    }
}
