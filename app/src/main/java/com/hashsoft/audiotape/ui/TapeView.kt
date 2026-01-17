package com.hashsoft.audiotape.ui

import android.app.Activity
import androidx.collection.mutableIntSetOf
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hashsoft.audiotape.data.AudioTapeListSortOrder
import com.hashsoft.audiotape.data.ItemStatus
import com.hashsoft.audiotape.ui.dialog.DeleteTapeConfirmDialog
import com.hashsoft.audiotape.ui.list.TapeList
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme

@Composable
fun TapeView(
    viewModel: TapeViewModel = hiltViewModel(),
    onAudioTransfer: () -> Unit = {},
    onDisplayMessage: (String) -> Unit = {},
    onFolderOpen: () -> Unit = {}
) {
    val displayTapePair by viewModel.displayTapeListState.collectAsStateWithLifecycle()
    val viewMode by viewModel.viewMode.collectAsStateWithLifecycle()
    val deleteIdsSet by viewModel.deleteIdsSet.collectAsStateWithLifecycle()
    val showConfirmDialog = remember { mutableStateOf(false) }

    val (displayTapeList, sortOrder) = displayTapePair
    val deleteMode = viewMode == TapeViewMode.DeleteTape
    val context = LocalContext.current

    DisposableEffect(Unit) {
        onDispose {
            val activity = context as? Activity
            if (activity != null && !activity.isChangingConfigurations) {
                viewModel.resetViewMode()
                viewModel.resetDeleteIds()
            }
        }
    }

    TapeList(
        modifier = Modifier.fillMaxSize(),
        displayTapeList = displayTapeList,
        sortOrder = sortOrder.ordinal,
        deleteMode = deleteMode,
        deleteIdsSet = deleteIdsSet,
        onSortChange = {
            viewModel.saveTapeListSortOrder(AudioTapeListSortOrder.fromInt(it))
        },
        onDeleteClick = {
            viewModel.switchDeleteTapeMode()
        },
        onCloseSelected = {
            viewModel.resetViewMode()
            viewModel.resetDeleteIds()
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
                onDisplayMessage = onDisplayMessage,
                onFolderOpen
            )
        }
    )

    if (showConfirmDialog.value) {
        DeleteTapeConfirmDialog(
            onConfirmResult = {
                viewModel.deleteSelectedTape {
                    showConfirmDialog.value = false
                    viewModel.resetViewMode()
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
    onDisplayMessage: (String) -> Unit = {},
    onFolderOpen: () -> Unit = {}
) {
    when (argument) {
        is AudioCallbackArgument.TapeSelected -> {
            val displayTape = displayTapeList.getOrNull(argument.index) ?: return
            // 遷移時は遷移後の画面で再生不可にするので許可する
            val status = displayTape.status
            if (status != ItemStatus.Normal && status != ItemStatus.Warning && !argument.transfer) {
                onDisplayMessage(displayTape.audioTape.folderPath)
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