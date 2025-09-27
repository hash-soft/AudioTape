package com.hashsoft.audiotape.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hashsoft.audiotape.data.LibraryStateDto
import com.hashsoft.audiotape.data.LibraryTab
import com.hashsoft.audiotape.data.PlayAudioDto
import com.hashsoft.audiotape.ui.item.SimpleAudioPlayItem
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun LibrarySheetRoute(
    viewModel: LibraryStateViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playItem by viewModel.playItemState.item.collectAsStateWithLifecycle()

    val isReady by viewModel.controllerOk.collectAsStateWithLifecycle()
    Timber.d("#LisReady $isReady")

    when (val state = uiState) {
        is LibraryStateUiState.Loading -> {}
        is LibraryStateUiState.Success -> LibrarySheetPager(
            state.libraryState,
            playItem = playItem,
            audioCallback = { argument -> playItemSelected(viewModel, argument) },
            tabs = viewModel.tabs()
        ) {
            viewModel.saveSelectedTabName(it)
        }
    }
}

private fun playItemSelected(
    viewModel: LibraryStateViewModel,
    argument: AudioCallbackArgument
): AudioCallbackResult {
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

        is AudioCallbackArgument.OpenAudioPlay -> {
            viewModel.saveSelectedPlayViewVisible(true)
            AudioCallbackResult.None
        }

        is AudioCallbackArgument.CloseAudioPlay -> {
            viewModel.saveSelectedPlayViewVisible(false)
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
    playItem: PlayAudioDto? = null,
    audioCallback: (AudioCallbackArgument) -> AudioCallbackResult = { AudioCallbackResult.None },
    onTabChange: (index: Int) -> Unit,
) {
    val state = rememberPagerState(initialPage = libraryState.selectedTabIndex) { tabs.size }
    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
        bottomBar = if (playItem == null) {
            {}
        } else {
            {
                SimpleAudioPlayItem(
                    path = playItem.path,
                    isReadyOk = playItem.isReadyOk,
                    isPlaying = playItem.isPlaying,
                    durationMs = playItem.durationMs,
                    contentPosition = playItem.contentPosition,
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

    if (libraryState.playViewVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                Timber.d("@@@dismiss")
                audioCallback(AudioCallbackArgument.CloseAudioPlay)
            },
            modifier = Modifier.fillMaxSize(),//.padding(WindowInsets.systemBars.asPaddingValues()),
            sheetState = sheetState,
            dragHandle = null,
            scrimColor = Color.Transparent,
            shape = RectangleShape
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Bottom Sheet Content") },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                },

                content = { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AudioPlayHomeRoute()
                    }
                }
            )


        }
    }
}


@Preview(showBackground = true)
@Composable
fun LibrarySheetPagerPreview() {
    //LibraryView()
}
