package com.hashsoft.audiotape.ui.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hashsoft.audiotape.logic.TimeFormat

@Composable
fun AudioDurationText(duration: Long, modifier: Modifier = Modifier) {
    Text(
        TimeFormat.formatMillis(duration),
        modifier = modifier
    )
}