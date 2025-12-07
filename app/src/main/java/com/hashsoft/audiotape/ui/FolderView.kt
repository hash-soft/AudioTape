package com.hashsoft.audiotape.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import com.example.directorytest.ui.view.AddressBar
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.ControllerState
import com.hashsoft.audiotape.data.PlayingStateDto
import com.hashsoft.audiotape.data.StorageItem
import com.hashsoft.audiotape.data.StorageLocationDto
import com.hashsoft.audiotape.ui.list.FolderList
import timber.log.Timber

@Composable
fun FolderViewRoute(
    viewModel: FolderViewModel = hiltViewModel(),
    onAudioTransfer: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val storageLocationList by viewModel.addressBarState.list.collectAsStateWithLifecycle()
    val displayFolder by viewModel.displayFolderState.collectAsStateWithLifecycle()
    val available by viewModel.availableState.collectAsStateWithLifecycle()

    Timber.d("state changed: $state")

    when (state) {
        FolderViewState.Start -> {}
        else -> {
            FolderView(
                addressList = storageLocationList,
                itemList = displayFolder.list,
                expandIndexList = displayFolder.expandIndexList,
                audioTape = displayFolder.audioTape,
                controllerState = displayFolder.controllerState,
                playingState = displayFolder.playingState,
                onFolderClick = viewModel::saveSelectedPath
            ) { argument ->
                when (argument) {

                    is AudioCallbackArgument.AudioSelected -> {
                        if(!available && !argument.transfer){
                            // Todo トーストを出したい
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
    addressList: List<StorageLocationDto>,
    itemList: List<StorageItem> = listOf(),
    expandIndexList: List<Int> = listOf(),
    audioTape: AudioTapeDto? = null,
    controllerState: ControllerState = ControllerState(
        playbackState = Player.STATE_IDLE,
        isPlaying = false
    ),
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
                storageItemList = itemList,
                expandIndexList = expandIndexList,
                isPlaying = controllerState.isPlaying,
                isCurrent = audioTape?.folderPath == playingState.folderPath,
                targetName = audioTape?.currentName ?: "",
                contentPosition = audioTape?.position ?: 0,
                audioCallback = audioCallback
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FolderViewPreview() {
    //FolderView()
}