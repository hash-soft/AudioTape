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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.data.DisplayPlayingSource
import com.hashsoft.audiotape.data.LibraryStateDto
import com.hashsoft.audiotape.data.LibraryTab
import com.hashsoft.audiotape.logic.StorageHelper
import com.hashsoft.audiotape.ui.item.SimpleAudioPlayItem
import kotlinx.coroutines.launch

@Composable
fun LibrarySheetRoute(
    libraryState: LibraryStateDto,
    tabs: List<LibraryTab>,
    viewMode: LibraryHomeViewMode,
    onTabChange: (index: Int) -> Unit,
    onTapeCallback: (TapeCallbackArgument) -> Unit,
    viewModel: LibrarySheetViewModel = hiltViewModel(),
    onAudioPlayClick: () -> Unit = {}
) {
    val playingPosition by viewModel.currentPositionState.collectAsStateWithLifecycle()
    val displayPlayingSource by viewModel.displayPlayingSource.collectAsStateWithLifecycle()
    val displayPlayingItem by viewModel.displayPlayingState.collectAsStateWithLifecycle()
    val available by viewModel.availableState.collectAsStateWithLifecycle()

    LibrarySheetPager(
        libraryState,
        tabs = tabs,
        viewMode = viewMode,
        isAvailable = available,
        displayPlaying = displayPlayingSource,
        displayPlayingItem = displayPlayingItem,
        playingPosition = playingPosition,
        onTabChange = onTabChange,
        onTapeCallback = onTapeCallback,
        audioCallback = { argument ->
            if (argument is AudioCallbackArgument.TransferAudioPlay) {
                onAudioPlayClick()
                return@LibrarySheetPager
            }
            playItemSelected(viewModel, displayPlayingItem, argument)
        }
    )

}

private fun playItemSelected(
    viewModel: LibrarySheetViewModel,
    displayPlayingItem: DisplayPlayingItem?,
    argument: AudioCallbackArgument
) {
    if (displayPlayingItem == null) return
    when (argument) {

        is AudioCallbackArgument.SeekTo -> {
            viewModel.seekTo(argument.position)
        }

        is AudioCallbackArgument.PlayPause -> {
            if (argument.isPlaying) {
                viewModel.pause()
            } else {
                viewModel.setPlayingParameters(displayPlayingItem.audioTape)
                viewModel.play()
            }
        }

        is AudioCallbackArgument.SkipNext -> {
            viewModel.seekToNext()
        }

        is AudioCallbackArgument.SkipPrevious -> {
            viewModel.seekToPrevious()
        }

        else -> {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibrarySheetPager(
    libraryState: LibraryStateDto,
    tabs: List<LibraryTab>,
    viewMode: LibraryHomeViewMode,
    isAvailable: Boolean,
    displayPlaying: DisplayPlayingSource,
    displayPlayingItem: DisplayPlayingItem?,
    playingPosition: Long,
    onTabChange: (index: Int) -> Unit,
    onTapeCallback: (TapeCallbackArgument) -> Unit,
    audioCallback: (AudioCallbackArgument) -> Unit
) {
    val state = rememberPagerState(initialPage = libraryState.selectedTabIndex) { tabs.size }
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(state) {
        // 完全にページが切り替わったら変化
        snapshotFlow { state.settledPage }.collect { page ->
            onTabChange(page)
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
                val audioItem =
                    displayPlayingItem.audioList.find { it.name == audioTape.currentName }
                val contentPosition =
                    if (playingPosition >= 0) playingPosition else audioTape.position
                SimpleAudioPlayItem(
                    directory = StorageHelper.treeListToString(
                        displayPlayingItem.treeList,
                        stringResource(R.string.path_separator),
                        default = audioTape.folderPath
                    ),
                    name = audioTape.currentName,
                    isAvailable = isAvailable,
                    displayPlaying = displayPlaying,
                    durationMs = audioItem?.metadata?.duration ?: 0,
                    contentPosition = contentPosition,
                    enableTransfer = viewMode != LibraryHomeViewMode.DeleteTape,
                    status = displayPlayingItem.status,
                    audioCallback = audioCallback
                )
            }
        }, snackbarHost = { SnackbarHost(hostState = snackBarHostState) }) { innerPadding ->
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
                    0 -> FolderViewRoute {
                        audioCallback(AudioCallbackArgument.TransferAudioPlay)
                    }

                    1 -> TapeView(
                        deleteMode = viewMode == LibraryHomeViewMode.DeleteTape,
                        onTapeCallback = onTapeCallback,
                        onAudioTransfer = { audioCallback(AudioCallbackArgument.TransferAudioPlay) },
                        onDisplaySnackBar = {
                            scope.launch {
                                snackBarHostState.showSnackbar(
                                    message = context.getString(R.string.this_no_audio, it),
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }) {
                        scope.launch {
                            state.animateScrollToPage(0)
                        }
                    }
                }
            }
        }
    }
}
