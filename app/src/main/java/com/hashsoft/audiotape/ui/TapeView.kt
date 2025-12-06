package com.hashsoft.audiotape.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hashsoft.audiotape.data.DisplayAudioTape
import com.hashsoft.audiotape.ui.list.TapeList

@Composable
fun TapeView(
    viewModel: TapeViewModel = hiltViewModel(),
    onFolderOpen: () -> Unit = {}
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
                        argument,
                        onFolderOpen
                    )
                }
            )
        }
    }
}

private fun tapeItemSelected(
    viewModel: TapeViewModel,
    audioTapeList: List<DisplayAudioTape>,
    argument: AudioCallbackArgument,
    onFolderOpen: () -> Unit = {}
) {
    when (argument) {
        is AudioCallbackArgument.TapeSelected -> {
            val tape = audioTapeList[argument.index].base
            viewModel.updatePlayingFolderPath(tape.folderPath)
            if (!viewModel.setCurrentMediaItemsPosition(tape)) {
                viewModel.switchPlayingFolder(tape)
            }
            viewModel.setPlayingParameters(tape)
            viewModel.playWhenReady(true)
        }

        is AudioCallbackArgument.TapeFolderOpen -> {
            // 選択フォルダパスを変更してから上位のページャー切り替えをコールバックする
            viewModel.saveSelectedPath(argument.path)
            onFolderOpen()
        }

        else -> {}
    }
}


@Preview(showBackground = true)
@Composable
fun TapeViewPreview() {
    TapeView()
}