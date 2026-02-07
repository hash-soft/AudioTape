package com.hashsoft.audiotape.ui

import android.content.Intent
import androidx.collection.intSetOf
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerArrayResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.Route
import com.hashsoft.audiotape.data.LibraryStateDto
import com.hashsoft.audiotape.data.LibraryTab
import com.hashsoft.audiotape.ui.dropdown.TextDropdownSelector
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme
import kotlinx.coroutines.launch


private enum class MenuIndex {
    UserSettings,
    Eject,
    LunchExternalFileApp,
    //About
}

@Composable
fun LibraryHomeRoute(
    viewModel: LibraryHomeViewModel = hiltViewModel(),
    onTransferClick: (route: Any) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is LibraryHomeUiState.Loading -> {}
        is LibraryHomeUiState.Success -> LibraryHome(
            state.libraryState,
            tabs = viewModel.tabs(),
            onTransferClick = {
                onTransferClick(it)
            },
            onTabChange = {
                if (it != viewModel.selectedTabIndex()) {
                    viewModel.saveSelectedTabName(it)
                }
            }
        )
    }
}

@Composable
private fun LibraryHome(
    libraryState: LibraryStateDto,
    tabs: List<LibraryTab>,
    viewModel: LibrarySheetViewModel = hiltViewModel(),
    onTransferClick: (route: Any) -> Unit = {},
    onTabChange: (index: Int) -> Unit = {},
) {
    val playingPosition by viewModel.currentPositionState.collectAsStateWithLifecycle()
    val displayPlayingSource by viewModel.displayPlayingSource.collectAsStateWithLifecycle()
    val displayPlayingItemResult by viewModel.displayPlayingState.collectAsStateWithLifecycle()
    val available by viewModel.availableState.collectAsStateWithLifecycle()

    val displayPlayingItem = when (val result = displayPlayingItemResult) {
        is PlayItemStateResult.Success -> result.displayPlayingItem
        else -> null
    }

    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            val context = LocalContext.current
            val okText = stringResource(R.string.ok)
            val snackBarMessage = stringResource(R.string.file_app_not_fount)
            NormalTopBar(displayPlayingItem != null) {
                when (it) {
                    MenuIndex.UserSettings.ordinal -> onTransferClick(Route.UserSettings)
                    MenuIndex.Eject.ordinal -> viewModel.ejectTape(displayPlayingItem?.audioTape)
                    MenuIndex.LunchExternalFileApp.ordinal -> {

                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            addCategory(Intent.CATEGORY_DEFAULT)
                            setDataAndType(null, "vnd.android.document/directory")
                        }
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            scope.launch {
                                snackBarHostState.showSnackbar(
                                    message = snackBarMessage,
                                    actionLabel = okText,
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    }

                    else -> {}
                }
            }
        }, snackbarHost = { SnackbarHost(hostState = snackBarHostState) }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            LibrarySheetRoute(
                libraryState,
                tabs,
                isAvailable = available,
                displayPlaying = displayPlayingSource,
                displayPlayingItem = displayPlayingItem,
                playingPosition = playingPosition,
                onTabChange
            ) { argument ->
                playItemSelected(viewModel, displayPlayingItem, argument, onTransferClick)
            }
        }
    }
}

private fun playItemSelected(
    viewModel: LibrarySheetViewModel,
    displayPlayingItem: DisplayPlayingItem?,
    argument: AudioCallbackArgument,
    onTransferClick: (route: Any) -> Unit = {}
) {
    if (displayPlayingItem == null) return
    when (argument) {

        is AudioCallbackArgument.TransferAudioPlay -> {
            onTransferClick(Route.AudioPlay)
        }

        is AudioCallbackArgument.SeekTo -> {
            // 簡易プレイヤーにシークはない
            viewModel.seekTo(argument.position)
        }

        is AudioCallbackArgument.PlayPause -> {
            if (argument.isPlaying) {
                viewModel.pause()
            } else {
                viewModel.setPlayingParameters(displayPlayingItem.audioTape)
                viewModel.playWhenReady(true)
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


@Composable
private fun NormalTopBar(
    ejectEnabled: Boolean,
    onClick: (Int) -> Unit = {}
) {
    @OptIn(ExperimentalMaterial3Api::class)
    TopAppBar(
        title = { Text(text = stringResource(R.string.app_name)) },
        actions = { MenuDropdownSelector(ejectEnabled, onClick) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    )
}

@Composable
private fun MenuDropdownSelector(ejectEnabled: Boolean, onClick: (Int) -> Unit = {}) {
    val (menuLabels, menuIndex) =
        stringArrayResource(R.array.menu_labels).toList() to
                integerArrayResource(R.array.menu_index).toList()

    TextDropdownSelector(
        menuLabels,
        "",
        selectedIndex = -1,
        disableMenuIds = if (ejectEnabled) intSetOf() else intSetOf(MenuIndex.Eject.ordinal),
        buttonContent = {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.menu_description),
            )
        }) {
        onClick(menuIndex.getOrElse(it) { -1 })
    }
}


@Preview(showBackground = true)
@Composable
fun LibraryHomePreview() {
    AudioTapeTheme {
        LibraryHome(
            LibraryStateDto(0),
            listOf()
        )
    }
}