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
import com.hashsoft.audiotape.data.PlayAudioDto
import com.hashsoft.audiotape.data.StorageAddressRepository
import com.hashsoft.audiotape.data.StorageItemDto
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
            _resumeAudioRepository = application.resumeAudioRepository
        )
    }
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
    val typeIndexList by viewModel.folderListState.typeIndexList.collectAsStateWithLifecycle()
    val playItem by viewModel.playItemState.item.collectAsStateWithLifecycle()

    Timber.d("state changed: $state")
    Timber.d("playItem changed $playItem")
    Timber.d("##isReady: $isReady")

    when (state) {
        FolderViewState.Start -> {}
        else -> {
            FolderView(
                selectedPath, storageLocationList, storageItemList,
                playItem = playItem,
                onFolderClick = viewModel::saveSelectedPath
            ) { argument ->
                when (argument) {
                    is AudioCallbackArgument.Display -> {
                        viewModel.loadMetadata(argument.index)
                    }

                    is AudioCallbackArgument.AudioSelected -> {
                        viewModel.updatePlayingFolderPath(selectedPath)
                        viewModel.setMediaItemsInFolderList(argument.index, 0)
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
    selectedPath: String,
    addressList: List<StorageLocationDto>,
    itemList: List<StorageItemDto> = listOf(),
    playItem: PlayAudioDto? = null,
    //navController: NavHostController = LocalNavController.current,
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
            // 再生状態だけが可変なのでリスト以外の細かい情報は不要
            // controllerModelViewから変換したリストを取得したほうがいい気がする
            // いや、プレイ中とカレントのインデックスを渡せばすむか
            // カレントはリジューム情報がいるからやっぱりほかにもいるな
            // sealedInterfaceをつけるか
            FolderList(
                modifier = Modifier.padding(innerPadding),
                storageItemList = itemList,
                audioCallback = audioCallback
            )
            /*   { argument ->
                   // bottomBarのほうとで役割が違う
                   when (argument) {
                       is AudioCallbackArgument.Display -> {
                           onDisplayFile(argument.index)
                       }

                       is AudioCallbackArgument.AudioSelected -> {
                           audioController.setMediaItems(itemList, 0, 0)
   //                        controllerViewModel.onAudioSelected(
   //                            uiState.folderState.selectedPath,
   //                            uiState.storageItemList,
   //                            argument
   //                        )
                           // mediaListになければつめなおしとかいろいろやらないといけないが遷移先でやったほうがよさそう
   //                        navController.navigate(
   //                            Route.AudioPlay(
   //                                selectedPath,
   //                                argument.name
   //                            )
   //                        )

                       }
               }*/
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FolderViewPreview() {
    //FolderView()
}