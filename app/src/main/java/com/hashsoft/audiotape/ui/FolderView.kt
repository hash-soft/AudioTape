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
import com.example.directorytest.ui.view.AddressBar
import com.hashsoft.audiotape.data.DisplayStorageItem
import com.hashsoft.audiotape.data.StorageLocationDto
import com.hashsoft.audiotape.ui.list.FolderList
import timber.log.Timber

@Composable
fun FolderViewRoute(
    viewModel: FolderViewModel = hiltViewModel()
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