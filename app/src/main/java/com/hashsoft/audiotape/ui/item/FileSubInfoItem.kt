package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hashsoft.audiotape.ui.text.SizeAndLastModifiedText

@Composable
fun FileSubInfoItem(size: Long, lastModified: Long) {
    Box(modifier = Modifier.fillMaxWidth()) {
        SizeAndLastModifiedText(size, lastModified, Modifier.align(Alignment.CenterEnd))
    }
}