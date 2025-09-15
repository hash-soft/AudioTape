package com.hashsoft.audiotape.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashsoft.audiotape.AudioTape
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.ui.list.TapeList

@Composable
fun TapeView(
    controller: AudioController,
    tapeViewModel: TapeViewModel = viewModel {
        val application = get(APPLICATION_KEY) as AudioTape
        TapeViewModel(
            controller,
            audioTapeRepository = application.databaseContainer.audioTapeRepository,
            _playingStateRepository = application.playingStateRepository,
            _playbackRepository = application.playbackRepository
        )
    }
) {
    val uiState by tapeViewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is TapeUiState.Loading -> {}
        is TapeUiState.Success -> TapeList(
            state.audioTapeList,
            audioCallback = { argument ->
                tapeItemSelected(
                    tapeViewModel,
                    state.audioTapeList,
                    argument
                )
            }
        )
    }
}

private fun tapeItemSelected(
    viewModel: TapeViewModel,
    audioTapeList: List<AudioTapeDto>,
    argument: AudioCallbackArgument
): AudioCallbackResult {
    return when (argument) {
        is AudioCallbackArgument.TapeSelected -> {
            val tape = audioTapeList[argument.index]
            viewModel.updatePlayingFolderPath(tape.folderPath)
            viewModel.setMediaItemsByTape(tape)
            viewModel.play()
            AudioCallbackResult.None
        }

        else -> AudioCallbackResult.None
    }
}


@Preview(showBackground = true)
@Composable
fun TapeViewPreview() {
    TapeView(AudioController())
}