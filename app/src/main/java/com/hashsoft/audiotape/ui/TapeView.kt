package com.hashsoft.audiotape.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hashsoft.audiotape.AudioTape
import com.hashsoft.audiotape.ui.list.TapeList

@Composable
fun TapeView(
    tapeViewModel: TapeViewModel = viewModel {
        val application = get(APPLICATION_KEY) as AudioTape
        TapeViewModel(
            audioTapeRepository = application.databaseContainer.audioTapeRepository
        )
    }
) {
    val uiState by tapeViewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is TapeUiState.Loading -> {}
        is TapeUiState.Success -> TapeList(
            state.audioTapeList
        )
    }
}


@Preview(showBackground = true)
@Composable
fun TapeViewPreview() {
    TapeView()
}