package com.hashsoft.audiotape.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.directorytest.ui.view.AddressBar
import com.hashsoft.audiotape.AudioTape
import com.hashsoft.audiotape.data.DisplayStorageItem
import com.hashsoft.audiotape.data.StorageAddressRepository
import com.hashsoft.audiotape.data.StorageItemListRepository
import com.hashsoft.audiotape.data.StorageLocationDto
import com.hashsoft.audiotape.ui.list.FolderList
import timber.log.Timber

@Composable
fun FolderViewRoute(
    controller: AudioController = AudioController(),
    viewModel: FolderViewModel = viewModel {
        val application = get(APPLICATION_KEY) as AudioTape
        FolderViewModel(
            _controller = controller,
            _folderStateRepository = application.libraryFolderRepository,
            storageAddressRepository = StorageAddressRepository(application),
            storageItemListRepository = StorageItemListRepository(application),
            _audioTapeRepository = application.databaseContainer.audioTapeRepository,
            _playingStateRepository = application.playingStateRepository,
            _playbackRepository = application.playbackRepository
        )
    },
    //navController: NavHostController = LocalNavController.current,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val selectedPath by viewModel.selectedPath.collectAsStateWithLifecycle()
    val storageLocationList by viewModel.addressBarState.list.collectAsStateWithLifecycle()
    val storageItemList by viewModel.folderListState.list.collectAsStateWithLifecycle()

    Timber.d("state changed: $state")

    when (state) {
        FolderViewState.Start -> {}
        else -> {
            FolderView(
                storageLocationList, storageItemList,
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

                    else -> {}

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
    onFolderClick: (String) -> Unit = {},
    audioCallback: (AudioCallbackArgument) -> AudioCallbackResult = { AudioCallbackResult.None }
) {
    Scaffold(
        // パディング不要なので消去
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
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