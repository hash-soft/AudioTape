package com.hashsoft.audiotape.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.RouteStateDto
import com.hashsoft.audiotape.data.RouteStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ルートコンテンツのViewModel
 * アプリケーションのナビゲーションルートの状態を管理する
 *
 * @param _controller オーディオコントローラー
 * @param _routeStateRepository ルート状態リポジトリ
 */
@HiltViewModel
class RouteContentViewModel @Inject constructor(private val _routeStateRepository: RouteStateRepository) :
    ViewModel() {

    /**
     * UIの状態
     */
    val uiState: MutableState<RouteStateUiState> = mutableStateOf(RouteStateUiState.Loading)

    /**
     * 初期化時にルート状態をリポジトリから取得し、UIの状態を更新する
     */
    init {
        viewModelScope.launch {
            val state = _routeStateRepository.getRouteState()
            uiState.value = RouteStateUiState.Success(state)
        }
    }

    /**
     * 開始画面を保存する
     *
     * @param startScreen 開始画面のルート
     */
    fun saveStartScreen(startScreen: String) = viewModelScope.launch {
        _routeStateRepository.saveStartScreen(startScreen)
    }
}

/**
 * ルート状態のUIの状態を表すインターフェース
 */
sealed interface RouteStateUiState {
    /**
     * ローディング状態
     */
    data object Loading : RouteStateUiState

    /**
     * 成功状態
     *
     * @property routeState ルートの状態
     */
    data class Success(
        val routeState: RouteStateDto
    ) : RouteStateUiState
}
