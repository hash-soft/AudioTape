package com.hashsoft.audiotape.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.directorytest.ui.view.AddressBar
import com.hashsoft.audiotape.AudioTape
import com.hashsoft.audiotape.data.DisplayStorageItem
import com.hashsoft.audiotape.data.PlayAudioDto
import com.hashsoft.audiotape.data.StorageAddressRepository
import com.hashsoft.audiotape.data.StorageItemListRepository
import com.hashsoft.audiotape.data.StorageLocationDto
import com.hashsoft.audiotape.ui.item.AudioPlayItem
import com.hashsoft.audiotape.ui.list.FolderList
import timber.log.Timber

@Composable
fun FolderViewRoute(
    viewModel: FolderViewModel = viewModel {
        val application = get(APPLICATION_KEY) as AudioTape
        FolderViewModel(
            _folderStateRepository = application.libraryFolderRepository,
            storageAddressRepository = StorageAddressRepository(application),
            storageItemListRepository = StorageItemListRepository(application),
            _audioTapeRepository = application.databaseContainer.audioTapeRepository,
            _playingStateRepository = application.playingStateRepository,
            _playbackRepository = application.playbackRepository,
            resumeAudioRepository = application.resumeAudioRepository
        )
    },
    //navController: NavHostController = LocalNavController.current,
) {
    val context = LocalContext.current
    LifecycleStartEffect(Unit) {
        Timber.d("start")
        viewModel.buildController(context)
        onStopOrDispose {
            Timber.d("stop")
            viewModel.releaseController()
        }
    }

    val isReady by viewModel.isReady.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val selectedPath by viewModel.selectedPath.collectAsStateWithLifecycle()
    val storageLocationList by viewModel.addressBarState.list.collectAsStateWithLifecycle()
    val storageItemList by viewModel.folderListState.list.collectAsStateWithLifecycle()
    val playItem by viewModel.playItemState.item.collectAsStateWithLifecycle()

    Timber.d("state changed: $state")
    Timber.d("playItem changed $playItem")
    Timber.d("##isReady: $isReady")

    when (state) {
        FolderViewState.Start -> {}
        else -> {
            FolderView(
                storageLocationList, storageItemList,
                playItem = playItem,
                onFolderClick = viewModel::saveSelectedPath
            ) { argument ->
                when (argument) {
                    is AudioCallbackArgument.Display -> {
                        viewModel.loadMetadata(argument.index)
                    }

                    is AudioCallbackArgument.AudioSelected -> {
                        viewModel.updatePlayingFolderPath(selectedPath)
                        viewModel.setMediaItemsInFolderList(argument.index)
                        viewModel.play()
                    }

                    is AudioCallbackArgument.FolderSelected -> {
                        viewModel.saveSelectedPath(argument.path)
                    }

                    is AudioCallbackArgument.Position -> {
                        return@FolderView AudioCallbackResult.Position(viewModel.getContentPosition())
                    }

                    is AudioCallbackArgument.SeekTo -> {
                        viewModel.seekTo(argument.position)
                    }

                    is AudioCallbackArgument.PlayPause -> {
                        if (argument.isPlaying) {
                            viewModel.pause()
                        } else {
                            viewModel.play()
                        }
                    }

                    // Todo 専用画面への遷移を追加
                }
                AudioCallbackResult.None
            }
        }
    }

}


@Composable
private fun FolderView(
    addressList: List<StorageLocationDto>,
    itemList: List<DisplayStorageItem> = listOf(),
    playItem: PlayAudioDto? = null,
    onFolderClick: (String) -> Unit = {},
    audioCallback: (AudioCallbackArgument) -> AudioCallbackResult = { AudioCallbackResult.None }
) {
    Scaffold(
        // パディング不要なので消去
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = if (playItem == null) {
            {}
        } else {
            {
                AudioPlayItem(
                    path = playItem.path,
                    isPlaying = playItem.isPlaying,
                    durationMs = playItem.durationMs,
                    contentPosition = playItem.contentPosition,
                    audioCallback = audioCallback
                )
            }
        }
    ) { innerPadding ->
        Column() {
            AddressBar(addressList, onFolderClick)
            FolderList(
                modifier = Modifier.padding(innerPadding),
                storageItemList = itemList,
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