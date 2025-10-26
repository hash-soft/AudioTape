package com.hashsoft.audiotape.ui.text

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.hashsoft.audiotape.logic.TimeFormat

@Composable
fun AudioDurationText(duration: Long, modifier: Modifier = Modifier, style: TextStyle = LocalTextStyle.current) {
    Text(
        TimeFormat.formatMillis(duration),
        modifier = modifier,
        style = style
    )
}