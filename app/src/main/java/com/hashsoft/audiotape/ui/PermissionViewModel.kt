/**
 * パーミッション関連のViewModelを定義するファイル
 */
package com.hashsoft.audiotape.ui

import androidx.lifecycle.ViewModel
import com.hashsoft.audiotape.data.AudioStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject


/**
 * パーミッションの状態を管理し、リポジトリとのやり取りを担うViewModel
 *
 * @property _audioStoreRepository 音声ストアへのアクセスを提供するリポジトリ
 */
@HiltViewModel
class PermissionViewModel @Inject constructor(private val _audioStoreRepository: AudioStoreRepository) :
    ViewModel() {

    /**
     * リポジトリを登録し、監視を開始する
     */
    fun register() {
        _audioStoreRepository.register()
    }

    /**
     * リポジトリを解放し、リソースをクリーンアップする
     */
    fun release() {
        _audioStoreRepository.release()
    }
}
