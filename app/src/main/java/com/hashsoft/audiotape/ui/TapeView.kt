package com.hashsoft.audiotape.ui

import androidx.collection.mutableIntSetOf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hashsoft.audiotape.ui.list.TapeList
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme

@Composable
fun TapeView(
    deleteMode: Boolean = false,
    viewModel: TapeViewModel = hiltViewModel(),
    onTapeCallback: (TapeCallbackArgument) -> Unit = {},
    onAudioTransfer: () -> Unit = {},
    onFolderOpen: () -> Unit = {}
) {
    val displayTapeList by viewModel.displayTapeListState.collectAsStateWithLifecycle()
    val deleteIdsSet by viewModel.deleteIdsSet.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetDeleteIds()
        }
    }

    onTapeCallback(TapeCallbackArgument.UpdateExist(displayTapeList.isNotEmpty()))

    TapeList(
        displayTapeList = displayTapeList,
        deleteMode = deleteMode,
        deleteIdsSet = deleteIdsSet,
        onCloseSelected = {
            viewModel.resetDeleteIds()
            onTapeCallback(TapeCallbackArgument.CloseSelected)
        },
        onCheckedChange = { checked, index ->
            if (checked) {
                viewModel.addDeleteId(index)
            } else {
                viewModel.removeDeleteId(index)
            }
        },
        onSelectedAllCheck = {
            val ids = mutableIntSetOf().apply {
                for (i in 0 until displayTapeList.size) {
                    add(i)
                }
            }
            viewModel.setDeletedIds(ids)
        },
        onTapeDelete = {
            viewModel.deleteSelectedTape {
                onTapeCallback(TapeCallbackArgument.CloseSelected)
            }
        },
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
    AudioTapeTheme {
        TapeView()
    }
}