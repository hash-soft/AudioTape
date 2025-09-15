package com.hashsoft.audiotape.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashsoft.audiotape.AudioTape
import com.hashsoft.audiotape.data.LibraryStateDto
import com.hashsoft.audiotape.data.LibraryTab
import com.hashsoft.audiotape.data.PlayAudioDto
import com.hashsoft.audiotape.ui.item.AudioPlayItem
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun LibrarySheetRoute(
    controller: AudioController = AudioController(),
    viewModel: LibraryStateViewModel = viewModel {
        val application = get(APPLICATION_KEY) as AudioTape
        LibraryStateViewModel(
            _controller = controller,
            _libraryStateRepository = application.libraryStateRepository,
            _playbackRepository = application.playbackRepository,
            audioTapeRepository = application.databaseContainer.audioTapeRepository,
            playingStateRepository = application.playingStateRepository,
            resumeAudioRepository = application.resumeAudioRepository
        )
    }
) {
    val uiState by viewModel.uiState
    val playItem by viewModel.playItemState.item.collectAsStateWithLifecycle()

    val isReady by controller.isReady.collectAsStateWithLifecycle()
    Timber.d("##isReady $isReady")

    when (val state = uiState) {
        is LibraryStateUiState.Loading -> {}
        is LibraryStateUiState.Success -> LibrarySheetPager(
            controller,
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

        // Todo 専用画面への遷移を追加

        else -> AudioCallbackResult.None
    }
}

@Composable
private fun LibrarySheetPager(
    controller: AudioController,
    libraryState: LibraryStateDto,
    tabs: List<LibraryTab>,
    playItem: PlayAudioDto? = null,
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
        }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TabRow(selectedTabIndex = state.currentPage) {
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
                    0 -> FolderViewRoute(controller)
                    1 -> TapeView()
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LibrarySheetPagerPreview() {
    //LibraryView()
}