package com.hashsoft.audiotape.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.data.LibraryStateDto
import com.hashsoft.audiotape.data.LibraryTab
import com.hashsoft.audiotape.logic.StorageHelper
import com.hashsoft.audiotape.ui.item.SimpleAudioPlayItem
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun LibrarySheetRoute(
    viewModel: LibraryStateViewModel = hiltViewModel(),
    onAudioPlayClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState
    val displayPlayingItem by viewModel.displayPlayingState.collectAsStateWithLifecycle()
    val playingPosition by viewModel.playingPosition.collectAsStateWithLifecycle()

//    val isReady by viewModel.controllerOk.collectAsStateWithLifecycle()
//    Timber.d("#LisReady $isReady")


    when (val state = uiState) {
        is LibraryStateUiState.Loading -> {}
        is LibraryStateUiState.Success -> LibrarySheetPager(
            state.libraryState,
            displayPlayingItem = displayPlayingItem,
            playingPosition = playingPosition,
            audioCallback = { argument ->
                if (argument is AudioCallbackArgument.TransferAudioPlay) {
                    onAudioPlayClick()
                    return@LibrarySheetPager AudioCallbackResult.None
                }
                playItemSelected(viewModel, displayPlayingItem, argument)
            },
            tabs = viewModel.tabs()
        ) {
            viewModel.saveSelectedTabName(it)
        }
    }
}

private fun playItemSelected(
    viewModel: LibraryStateViewModel,
    displayPlayingItem: DisplayPlayingItem?,
    argument: AudioCallbackArgument
): AudioCallbackResult {
    if (displayPlayingItem == null) return AudioCallbackResult.None
    return when (argument) {
        is AudioCallbackArgument.Position -> {
            return AudioCallbackResult.Position(viewModel.getContentPosition())
        }

        is AudioCallbackArgument.SeekTo -> {
            viewModel.seekTo(argument.position)
            AudioCallbackResult.None
        }

        is AudioCallbackArgument.PlayPause -> {
            if (argument.isPlaying) {
                viewModel.pause()
            } else {
                viewModel.setPlayingParameters(displayPlayingItem.audioTape)
                viewModel.play()
            }
            AudioCallbackResult.None
        }

        is AudioCallbackArgument.SkipNext -> {
            viewModel.seekToNext()
            AudioCallbackResult.None
        }

        is AudioCallbackArgument.SkipPrevious -> {
            viewModel.seekToPrevious()
            AudioCallbackResult.None
        }

        else -> AudioCallbackResult.None
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibrarySheetPager(
    libraryState: LibraryStateDto,
    tabs: List<LibraryTab>,
    displayPlayingItem: DisplayPlayingItem?,
    playingPosition: Long,
    audioCallback: (AudioCallbackArgument) -> AudioCallbackResult = { AudioCallbackResult.None },
    onTabChange: (index: Int) -> Unit,
) {
    val state = rememberPagerState(initialPage = libraryState.selectedTabIndex) { tabs.size }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state) {
        // 完全にページが切り替わったら変化
        snapshotFlow { state.settledPage }.collect { page ->
            Timber.d("page changed $page")
            onTabChange(state.settledPage)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = if (displayPlayingItem == null) {
            {}
        } else {
            {
                val audioTape = displayPlayingItem.audioTape
                val audioItem = displayPlayingItem.audioItem
                val contentPosition =
                    if (playingPosition >= 0) playingPosition else audioTape.position
                SimpleAudioPlayItem(
                    directory = StorageHelper.treeListToString(
                        displayPlayingItem.treeList,
                        stringResource(R.string.path_separator),
                        default = audioTape.folderPath
                    ),
                    name = audioTape.currentName,
                    isReadyOk = displayPlayingItem.controllerState.isReadyOk,
                    isPlaying = displayPlayingItem.controllerState.isPlaying,
                    durationMs = audioItem?.metadata?.duration ?: 0,
                    contentPosition = contentPosition,
                    audioCallback = audioCallback
                )
            }
        }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            SecondaryTabRow(selectedTabIndex = state.currentPage) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = state.currentPage == index,
                        onClick = {
                            scope.launch {
                                state.animateScrollToPage(index)
                            }
                        },
                        text = { Text(tab.name) },
                        icon = { Icon(tab.icon, tab.name) }
                    )
                }
            }
            HorizontalPager(
                state = state,
                flingBehavior = PagerDefaults.flingBehavior(state, snapPositionalThreshold = 0.3f)
            ) {
                when (it) {
                    0 -> FolderViewRoute()
                    1 -> TapeView {
                        scope.launch {
                            state.animateScrollToPage(0)
                        }
                    }
                }
            }
        }
    }
}
