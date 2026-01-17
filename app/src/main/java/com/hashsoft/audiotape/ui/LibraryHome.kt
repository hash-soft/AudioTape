package com.hashsoft.audiotape.ui

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
import com.hashsoft.audiotape.data.LibraryStateDto
import com.hashsoft.audiotape.data.LibraryTab
import com.hashsoft.audiotape.ui.dropdown.TextDropdownSelector
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme


private enum class MenuIndex {
    UserSettings
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
    onTransferClick: (route: Any) -> Unit = {},
    onTabChange: (index: Int) -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            NormalTopBar(onTransferClick)
        }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            LibrarySheetRoute(libraryState, tabs, onTabChange) {
                onTransferClick(Route.AudioPlay)
            }
        }
    }
}

@Composable
private fun NormalTopBar(
    onTransferClick: (route: Any) -> Unit = {}
) {
    @OptIn(ExperimentalMaterial3Api::class)
    TopAppBar(
        title = { Text(text = stringResource(R.string.app_name)) },
        actions = {
            MenuDropdownSelector {
                when (it) {
                    MenuIndex.UserSettings.ordinal -> onTransferClick(Route.UserSettings)
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
private fun MenuDropdownSelector(onChange: (Int) -> Unit = {}) {
    val (menuLabels, menuIndex) =
        stringArrayResource(R.array.menu_labels).toList() to
                integerArrayResource(R.array.menu_index).toList()

    TextDropdownSelector(
        menuLabels,
        "",
        selectedIndex = -1,
        disableMenuIds = intSetOf(),
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
            LibraryStateDto(0),
            listOf()
        )
    }
}