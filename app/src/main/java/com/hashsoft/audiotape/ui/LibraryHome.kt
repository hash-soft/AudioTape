package com.hashsoft.audiotape.ui

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


@Composable
fun LibraryHomeRoute(
    viewModel: LibraryHomeViewModel = hiltViewModel(),
    onTransferClick: (route: Any) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val existTape by viewModel.existTape.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is LibraryHomeUiState.Loading -> {}
        is LibraryHomeUiState.Success -> LibraryHome(
            state.libraryState,
            tabs = viewModel.tabs(),
            onTransferClick = onTransferClick,
            existTape = existTape,
            onTabChange = {
                viewModel.saveSelectedTabName(it)
            },
            onExistTapeChange = { viewModel.updateExistTape(it) },
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
    onTransferClick: (route: Any) -> Unit = {},
    onTabChange: (index: Int) -> Unit = {},
    onExistTapeChange: (exist: Boolean) -> Unit = {},
    onSortChange: (sortOrder: AudioTapeListSortOrder) -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
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
                    MenuDropdownSelector {
                        when (it) {
                            0 -> onTransferClick(Route.UserSettings)
                            else -> {}
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            LibrarySheetRoute(libraryState, tabs, onTabChange, onExistTapeChange) {
                onTransferClick(Route.AudioPlay)
            }
        }
    }
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
    onChange: (Int) -> Unit = {}
) {
    val menuLabels = stringArrayResource(R.array.menu_labels).toList()

    TextDropdownSelector(menuLabels, "", selectedIndex = -1, iconContent = {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = stringResource(R.string.menu_description),
        )
    }) {
        onChange(it)
    }
}


@Preview(showBackground = true)
@Composable
fun LibraryHomePreview() {
    AudioTapeTheme {
        LibraryHome(LibraryStateDto(0, AudioTapeListSortOrder.NAME_ASC), listOf(), false)
    }
}