package com.hashsoft.audiotape.ui

import androidx.collection.intSetOf
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.integerArrayResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.Route
import com.hashsoft.audiotape.data.AudioTapeListSortOrder
import com.hashsoft.audiotape.data.LibraryStateDto
import com.hashsoft.audiotape.data.LibraryStateRepository
import com.hashsoft.audiotape.data.LibraryTab
import com.hashsoft.audiotape.ui.dropdown.TextDropdownSelector
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme


private enum class MenuIndex {
    UserSettings,
    Delete
}

@Composable
fun LibraryHomeRoute(
    viewModel: LibraryHomeViewModel = hiltViewModel(),
    onTransferClick: (route: Any) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val existTape by viewModel.existTape.collectAsStateWithLifecycle()
    val viewMode by viewModel.viewMode.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is LibraryHomeUiState.Loading -> {}
        is LibraryHomeUiState.Success -> LibraryHome(
            state.libraryState,
            tabs = viewModel.tabs(),
            onTransferClick = {
                viewModel.resetViewMode()
                onTransferClick(it)
            },
            existTape = existTape,
            viewMode = viewMode,
            onTabChange = {
                if (it != viewModel.selectedTabIndex()) {
                    viewModel.resetViewMode()
                    viewModel.saveSelectedTabName(it)
                }
            },
            onTapeCallback = {
                when (it) {
                    is TapeCallbackArgument.UpdateExist -> viewModel.updateExistTape(it.exist)
                    is TapeCallbackArgument.CloseSelected -> viewModel.updateViewMode(
                        LibraryHomeViewMode.Normal
                    )

                    is TapeCallbackArgument.DeleteSelected -> viewModel.updateViewMode(
                        LibraryHomeViewMode.DeleteTape
                    )
                }
            },
        ) {
            viewModel.saveTapeListSortOrder(it)
        }
    }
}

@Composable
private fun LibraryHome(
    libraryState: LibraryStateDto,
    tabs: List<LibraryTab>,
    existTape: Boolean,
    viewMode: LibraryHomeViewMode,
    onTransferClick: (route: Any) -> Unit = {},
    onTabChange: (index: Int) -> Unit = {},
    onTapeCallback: (TapeCallbackArgument) -> Unit = {},
    onSortChange: (sortOrder: AudioTapeListSortOrder) -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (viewMode == LibraryHomeViewMode.DeleteTape) {
                DeleteTapeTopBar()
            } else {
                NormalTopBar(
                    libraryState, existTape, onTransferClick, onTapeDelete = {
                        onTapeCallback(TapeCallbackArgument.DeleteSelected)
                    }, onSortChange
                )
            }
        }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            LibrarySheetRoute(libraryState, tabs, viewMode, onTabChange, onTapeCallback) {
                onTransferClick(Route.AudioPlay)
            }
        }
    }
}

@Composable
private fun NormalTopBar(
    libraryState: LibraryStateDto,
    existTape: Boolean,
    onTransferClick: (route: Any) -> Unit = {},
    onTapeDelete: () -> Unit = {},
    onSortChange: (sortOrder: AudioTapeListSortOrder) -> Unit = {}
) {
    @OptIn(ExperimentalMaterial3Api::class)
    TopAppBar(
        title = { Text(text = stringResource(R.string.app_name)) },
        actions = {
            if (libraryState.selectedTabIndex == LibraryStateRepository.TAPE_NAME_INDEX) {
                SortDropdownSelector(
                    libraryState.tapeListSortOrder,
                    enabled = existTape,
                    onSortChange
                )
            }
            MenuDropdownSelector(libraryState.selectedTabIndex, existTape) {
                when (it) {
                    MenuIndex.UserSettings.ordinal -> onTransferClick(Route.UserSettings)
                    MenuIndex.Delete.ordinal -> onTapeDelete()
                    else -> {}
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    )
}

@Composable
private fun DeleteTapeTopBar() {
    @OptIn(ExperimentalMaterial3Api::class)
    TopAppBar(
        title = { Text(text = stringResource(R.string.app_name)) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    )
}

@Composable
private fun SortDropdownSelector(
    selectedIndex: AudioTapeListSortOrder = AudioTapeListSortOrder.NAME_ASC,
    enabled: Boolean = true,
    onChange: (AudioTapeListSortOrder) -> Unit = {}
) {
    val sortLabels = stringArrayResource(R.array.tape_list_sort_labels).toList()

    TextDropdownSelector(
        sortLabels,
        "",
        selectedIndex = selectedIndex.ordinal,
        enabled,
        iconContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Sort,
                contentDescription = stringResource(R.string.menu_description),
            )
        }) {
        onChange(AudioTapeListSortOrder.fromInt(it))
    }
}

@Composable
private fun MenuDropdownSelector(
    tabIndex: Int,
    existTape: Boolean,
    onChange: (Int) -> Unit = {}
) {
    val tapeTab = tabIndex == LibraryStateRepository.TAPE_NAME_INDEX
    val (menuLabels, menuIndex) = if (tapeTab) {
        stringArrayResource(R.array.menu_labels).toList() to
                integerArrayResource(R.array.menu_index).toList()
    } else {
        stringArrayResource(R.array.menu_labels).filterIndexed { index, _ -> index != MenuIndex.Delete.ordinal } to
                integerArrayResource(R.array.menu_index).filterIndexed { index, _ -> index != MenuIndex.Delete.ordinal }
    }

    TextDropdownSelector(
        menuLabels,
        "",
        selectedIndex = -1,
        disableMenuIds = if (tapeTab && !existTape) intSetOf(MenuIndex.Delete.ordinal) else intSetOf(),
        iconContent = {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.menu_description),
            )
        }) {
        onChange(menuIndex.getOrElse(it) { -1 })
    }
}


@Preview(showBackground = true)
@Composable
fun LibraryHomePreview() {
    AudioTapeTheme {
        LibraryHome(
            LibraryStateDto(0, AudioTapeListSortOrder.NAME_ASC),
            listOf(),
            false,
            LibraryHomeViewMode.Normal
        )
    }
}