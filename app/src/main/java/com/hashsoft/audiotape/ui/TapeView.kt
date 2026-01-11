package com.hashsoft.audiotape.ui

import androidx.collection.mutableIntSetOf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hashsoft.audiotape.data.ItemStatus
import com.hashsoft.audiotape.ui.dialog.DeleteTapeConfirmDialog
import com.hashsoft.audiotape.ui.list.TapeList
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme

@Composable
fun TapeView(
    deleteMode: Boolean = false,
    viewModel: TapeViewModel = hiltViewModel(),
    onTapeCallback: (TapeCallbackArgument) -> Unit = {},
    onAudioTransfer: () -> Unit = {},
    onDisplaySnackBar: (String) -> Unit = {},
    onFolderOpen: () -> Unit = {}
) {
    val displayTapeList by viewModel.displayTapeListState.collectAsStateWithLifecycle()
    val deleteIdsSet by viewModel.deleteIdsSet.collectAsStateWithLifecycle()
    val showConfirmDialog = remember { mutableStateOf(false) }

    LaunchedEffect(deleteMode) {
        if (!deleteMode) {
            viewModel.resetDeleteIds()
        }
    }

    onTapeCallback(TapeCallbackArgument.UpdateExist(displayTapeList.isNotEmpty()))

    TapeList(
        displayTapeList = displayTapeList,
        deleteMode = deleteMode,
        deleteIdsSet = deleteIdsSet,
        onCloseSelected = {
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
        onTapeDelete = { showConfirmDialog.value = true },
        audioCallback = { argument ->
            tapeItemSelected(
                viewModel,
                displayTapeList,
                argument,
                onAudioTransfer = onAudioTransfer,
                onDisplaySnackBar = onDisplaySnackBar,
                onFolderOpen
            )
        }
    )
    if (showConfirmDialog.value) {
        DeleteTapeConfirmDialog(
            onConfirmResult = {
                viewModel.deleteSelectedTape {
                    showConfirmDialog.value = false
                    onTapeCallback(TapeCallbackArgument.CloseSelected)
                }
            },
            onDismissResult = { showConfirmDialog.value = false })
    }
}

private fun tapeItemSelected(
    viewModel: TapeViewModel,
    displayTapeList: List<DisplayTapeItem>,
    argument: AudioCallbackArgument,
    onAudioTransfer: () -> Unit = {},
    onDisplaySnackBar: (String) -> Unit = {},
    onFolderOpen: () -> Unit = {}
) {
    when (argument) {
        is AudioCallbackArgument.TapeSelected -> {
            val displayTape = displayTapeList.getOrNull(argument.index) ?: return
            // 遷移時は遷移後の画面で再生不可にするので許可する
            if (displayTape.status != ItemStatus.Normal && !argument.transfer) {
                onDisplaySnackBar(displayTape.audioTape.folderPath)
                return
            }
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