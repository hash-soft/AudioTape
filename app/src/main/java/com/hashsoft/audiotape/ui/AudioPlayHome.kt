package com.hashsoft.audiotape.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.hashsoft.audiotape.data.AudioTapeDto


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayHomeRoute() {
    LazyColumn {
        items(20) { index ->
            Text(text = "Item $index")
        }
    }
}
