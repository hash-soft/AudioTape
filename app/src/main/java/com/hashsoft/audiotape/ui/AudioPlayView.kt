package com.hashsoft.audiotape.ui

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.ui.PlayerView
import com.hashsoft.audiotape.AudioTape
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.ui.dropdown.AudioDropDown


@Composable
fun AudioPlayViewRoute(
) {
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//
//    when (val state = uiState) {
//        is AudioPlayUiState.Loading -> {}
//        is AudioPlayUiState.Success -> AudioPlayView(state)
//    }
}


@Composable
private fun AudioPlayView(uiState: AudioPlayUiState.Success) {
    val context = LocalContext.current
    //val controllerViewModel: AudioControllerViewModel = LocalAudioControllerViewModel.current
    Column {
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    //player = controllerViewModel.getController()
                    layoutParams =
                        FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            600
                        )
                }
            }
        )
//        AudioDropDown(uiState.currentPlay, uiState.audioItemList)
//        IconButton(onClick = { /*TODO*/ }) {
//            Icon(Icons.Filled.PlayArrow, contentDescription = "再生")
//            // pauseはそのままPauseで使える
//        }
    }
}
