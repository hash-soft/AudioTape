package com.hashsoft.audiotape.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hashsoft.audiotape.ui.list.TapeList

@Composable
fun TapeView(
    viewModel: TapeViewModel = hiltViewModel(),
    onAudioTransfer: () -> Unit = {},
    onFolderOpen: () -> Unit = {}
) {
    val displayTapeList by viewModel.displayTapeListState.collectAsStateWithLifecycle()

    TapeList(
        displayTapeList = displayTapeList,
        audioCallback = { argument ->
            tapeItemSelected(
                viewModel,
                displayTapeList,
                argument,
                onAudioTransfer = onAudioTransfer,
                onFolderOpen
            )
        }
    )
}

private fun tapeItemSelected(
    viewModel: TapeViewModel,
    displayTapeList: List<DisplayTapeItem>,
    argument: AudioCallbackArgument,
    onAudioTransfer: () -> Unit = {},
    onFolderOpen: () -> Unit = {}
) {
    when (argument) {
        is AudioCallbackArgument.TapeSelected -> {
            val displayTape = displayTapeList.getOrNull(argument.index) ?: return
            val tape = displayTape.audioTape
            viewModel.switchPlayingFolder(tape)
            viewModel.setPlayingParameters(tape)
            if (argument.transfer) {
                onAudioTransfer()
            } else {
                viewModel.playWhenReady(true)
            }
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