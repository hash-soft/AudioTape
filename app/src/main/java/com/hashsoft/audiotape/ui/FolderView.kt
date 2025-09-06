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
import androidx.navigation.NavHostController
import com.example.directorytest.ui.view.AddressBar
import com.hashsoft.audiotape.AudioTape
import com.hashsoft.audiotape.LocalNavController
import com.hashsoft.audiotape.data.StorageAddressRepository
import com.hashsoft.audiotape.data.StorageItemDto
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
            _folderStateRepository = application.libraryFolderRepository,
            storageAddressRepository = StorageAddressRepository(application),
            storageItemListRepository = StorageItemListRepository(application),
            _audioTapeRepository = application.databaseContainer.audioTapeRepository,
            _controller = controller
        )
    }
) {
    val context = LocalContext.current
    LifecycleStartEffect(Unit) {
        Timber.d("start")
        controller.buildController(context)
        onStopOrDispose {
            Timber.d("stop")
            controller.releaseController()
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val selectedPath by viewModel.selectedPath.collectAsStateWithLifecycle()
    val storageLocationList by viewModel.addressBarState.list.collectAsStateWithLifecycle()
    val storageItemList by viewModel.folderListState.list.collectAsStateWithLifecycle()
    val typeIndexList by viewModel.folderListState.typeIndexList.collectAsStateWithLifecycle()
    //val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Timber.d("state changed $state")
    when (state) {
        FolderViewState.Start -> {}
        else -> {
            FolderView(
                selectedPath, storageLocationList, storageItemList,
                onFolderClick = viewModel::saveSelectedPath
            ) { argument ->
                when (argument) {
                    is AudioCallbackArgument.Display -> {
                        viewModel.loadMetadata(argument.index)
                    }

                    is AudioCallbackArgument.AudioSelected -> {
                        viewModel.setMediaItemsInFolderList(argument.index, 10000)
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
    selectedPath: String,
    addressList: List<StorageLocationDto>,
    itemList: List<StorageItemDto> = listOf(),
//    viewModel: CurrentAudioViewModel = viewModel {
//        val application = get(APPLICATION_KEY) as AudioTape
//        CurrentAudioViewModel(
//            _playbackRepository = PlaybackRepository(),
//            _playingStateRepository = application.playingStateRepository,
//            _audioTapeRepository = application.databaseContainer.audioTapeRepository
//        )
//    },
    navController: NavHostController = LocalNavController.current,
    onFolderClick: (String) -> Unit = {},
    audioCallback: (AudioCallbackArgument) -> AudioCallbackResult = { AudioCallbackResult.None }
) {

    //val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        // パディング不要なので消去
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
//        bottomBar = {
//            val item = uiState.storageItemList.getOrNull(extra.index)
//            if (
//                uiStateController.data.currentMediaId.isNotEmpty()
//            ) {
//                AudioPlayItem(
//                    path = uiStateController.data.currentMediaId,
//                    isPlaying = uiStateController.data.isPlaying,
//                    isCurrent = item?.path == uiStateController.data.currentMediaId,
//                    isReady = true, // isReadyを常にfalseにしているため
//                    durationMs = controllerViewModel.getContentDuration(),
//                    contentPosition = uiStateController.data.contentPosition//controllerViewModel.getContentPosition()
//                ) { argument ->
//                    // こっちはカレント選択
//                    when (argument) {
//                        is AudioCallbackArgument.PlayPause -> {
//                            if (argument.isPlaying) {
//                                controllerViewModel.pause()
//                            } else {
//                                controllerViewModel.play()
//                            }
//                        }
//
//                        is AudioCallbackArgument.Position -> {
//                            return@AudioPlayItem AudioCallbackResult.Position(controllerViewModel.getContentPosition())
//                        }
//
//                        is AudioCallbackArgument.SeekTo -> {
//                            controllerViewModel.seekTo(argument.position)
//                        }
//
//                        else -> {}
//                    }
//                    AudioCallbackResult.None
//                }
//
//            }
//        }
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
                //storageItemExtra = extra
                audioCallback = audioCallback
            )
            /*   { argument ->
                   // bottomBarのほうとで役割が違う
                   when (argument) {
                       is AudioCallbackArgument.Display -> {
                           onDisplayFile(argument.index)
                       }
                       // Todo 専用画面にいくようにしたい
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

                       is AudioCallbackArgument.FolderSelected -> {
                           onFolderClick(argument.path)
                       }

                       else -> {}
                   }
                   AudioCallbackResult.None
               }*/
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FolderViewPreview() {
    //FolderView()
}