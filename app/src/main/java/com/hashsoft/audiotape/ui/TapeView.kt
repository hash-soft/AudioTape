package com.hashsoft.audiotape.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashsoft.audiotape.AudioTape
import com.hashsoft.audiotape.data.DisplayAudioTape
import com.hashsoft.audiotape.ui.list.TapeList

@Composable
fun TapeView(
    controller: AudioController,
    viewModel: TapeViewModel = viewModel {
        val application = get(APPLICATION_KEY) as AudioTape
        TapeViewModel(
            controller,
            audioTapeRepository = application.databaseContainer.audioTapeRepository,
            _playingStateRepository = application.playingStateRepository,
            _playbackRepository = application.playbackRepository
        )
    }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tapeListState by viewModel.tapeListState.list.collectAsStateWithLifecycle()

    when (state) {
        TapeViewState.Start -> {}
        else -> {
            TapeList(
                tapeListState,
                audioCallback = { argument ->
                    tapeItemSelected(
                        viewModel,
                        tapeListState,
                        argument
                    )
                }
            )
        }
    }
}

private fun tapeItemSelected(
    viewModel: TapeViewModel,
    audioTapeList: List<DisplayAudioTape>,
    argument: AudioCallbackArgument
): AudioCallbackResult {
    return when (argument) {
        is AudioCallbackArgument.TapeSelected -> {
            val tape = audioTapeList[argument.index].base
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