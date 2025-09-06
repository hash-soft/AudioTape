package com.hashsoft.audiotape.ui

import androidx.compose.foundation.layout.Column
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashsoft.audiotape.data.LibraryStateDto
import com.hashsoft.audiotape.data.LibraryTab
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun LibrarySheetRoute(
    libraryStateViewModel: LibraryStateViewModel = viewModel(factory = LibraryStateViewModel.Factory),
) {
    val uiState by libraryStateViewModel.uiState

    when (val state = uiState) {
        is LibraryStateUiState.Loading -> {}
        is LibraryStateUiState.Success -> LibrarySheetPager(
            state.libraryState,
            tabs = libraryStateViewModel.tabs()
        ) {
            libraryStateViewModel.saveSelectedTabName(it)
        }
    }
}

@Composable
private fun LibrarySheetPager(
    libraryState: LibraryStateDto,
    tabs: List<LibraryTab>,
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

    @Suppress("UnusedMaterial3ScaffoldPaddingParameter")
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column {
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
                    0 -> FolderViewRoute()
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