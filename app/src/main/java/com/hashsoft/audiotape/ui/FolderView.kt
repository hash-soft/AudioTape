package com.hashsoft.audiotape.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.PlayingStateDto
import com.hashsoft.audiotape.data.StorageItem
import com.hashsoft.audiotape.data.StorageLocationDto
import com.hashsoft.audiotape.ui.bar.AddressBar
import com.hashsoft.audiotape.ui.list.FolderList

@Composable
fun FolderViewRoute(
    viewModel: FolderViewModel = hiltViewModel(),
    onDisplayMessage: (Int) -> Unit = {},
    onAudioTransfer: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val storageLocationList by viewModel.addressBarState.list.collectAsStateWithLifecycle()
    val displayFolder by viewModel.displayFolderState.collectAsStateWithLifecycle()
    val available by viewModel.availableState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(viewModel) {
        viewModel.scrollRequest.collect {
            listState.scrollToItem(0)
        }
    }

    when (state) {
        FolderViewState.Start -> {}
        else -> {
            FolderView(
                listState = listState,
                addressList = storageLocationList,
                itemList = displayFolder.list,
                expandIndexList = displayFolder.expandIndexList,
                audioTape = displayFolder.audioTape,
                isPlaying = displayFolder.isPlaying,
                playingState = displayFolder.playingState,
                onFolderClick = viewModel::saveSelectedPath
            ) { argument ->
                when (argument) {

                    is AudioCallbackArgument.AudioSelected -> {
                        if (!available && !argument.transfer) {
                            onDisplayMessage(R.string.not_ready_to_play)
                            return@FolderView
                        }
                        val tape = viewModel.makeAudioTape(
                            displayFolder.audioTape,
                            displayFolder.settings,
                            displayFolder.folderPath,
                            argument.name
                        )

                        if (!viewModel.setCurrentMediaItemsPosition(
                                displayFolder.list,
                                argument.index,
                                argument.position
                            )
                        ) {
                            viewModel.switchPlayingFolder(tape, displayFolder.audioTape == null)
                        }

                        viewModel.setPlayingParameters(tape)
                        if (argument.transfer) {
                            onAudioTransfer()
                        } else {
                            viewModel.playWhenReady(true)
                        }
                    }

                    is AudioCallbackArgument.FolderSelected -> {
                        viewModel.saveSelectedPath(argument.path)
                    }

                    else -> {}

                }
            }
        }
    }

}


@Composable
private fun FolderView(
    listState: LazyListState,
    addressList: List<StorageLocationDto>,
    itemList: List<StorageItem> = listOf(),
    expandIndexList: List<Int> = listOf(),
    audioTape: AudioTapeDto? = null,
    isPlaying: Boolean = false,
    playingState: PlayingStateDto = PlayingStateDto(""),
    onFolderClick: (String) -> Unit = {},
    audioCallback: (AudioCallbackArgument) -> Unit
) {
    Scaffold(
        // パディング不要なので消去
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        Column {
            AddressBar(addressList, onFolderClick)
            FolderList(
                modifier = Modifier.padding(innerPadding),
                listState = listState,
                storageItemList = itemList,
                expandIndexList = expandIndexList,
                isPlaying = isPlaying,
                isCurrent = audioTape?.folderPath == playingState.folderPath,
                targetName = audioTape?.currentName ?: "",
                contentPosition = audioTape?.position ?: 0,
                audioCallback = audioCallback
            )
        }
    }
}
