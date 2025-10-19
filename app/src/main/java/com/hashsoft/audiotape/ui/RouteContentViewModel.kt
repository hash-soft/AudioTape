package com.hashsoft.audiotape.ui

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.RouteStateDto
import com.hashsoft.audiotape.data.RouteStateRepository
import com.hashsoft.audiotape.data.StorageVolumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RouteContentViewModel @Inject constructor(
    private val _controller: AudioController,
    private val _routeStateRepository: RouteStateRepository,
    private val _storageVolumeRepository: StorageVolumeRepository,
    private val _audioStoreRepository: AudioStoreRepository
) :
    ViewModel() {

    val uiState: MutableState<RouteStateUiState> = mutableStateOf(RouteStateUiState.Loading)

    init {
        viewModelScope.launch {
            //val volumes = _storageVolumeRepository.volumeChangeFlow().first()
            _audioStoreRepository.reload()
            val state = _routeStateRepository.getRouteState()
            uiState.value = RouteStateUiState.Success(state)
        }
    }

    fun buildController(context: Context) = _controller.buildController(context)
    fun releaseController() = _controller.releaseController()

    fun saveStartScreen(startScreen: String) = viewModelScope.launch {
        _routeStateRepository.saveStartScreen(startScreen)
    }
}

sealed interface RouteStateUiState {
    data object Loading : RouteStateUiState
    data class Success(
        val routeState: RouteStateDto
    ) : RouteStateUiState
}