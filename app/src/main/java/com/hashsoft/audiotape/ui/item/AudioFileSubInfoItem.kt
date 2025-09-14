package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hashsoft.audiotape.ui.text.AudioDurationText
import com.hashsoft.audiotape.ui.text.SizeAndLastModifiedText

@Composable
fun AudioFileSubInfoItem(
    size: Long,
    lastModified: Long,
    duration: Long,
    isResume: Boolean,
    contentPosition: Long
) {
    if (isResume) {
        AudioFileSubInfoItemTwoRow(size, lastModified, duration, contentPosition)
    } else {
        AudioFileSubInfoItemOneRow(size, lastModified, duration)
    }
}

@Composable
private fun AudioFileSubInfoItemOneRow(size: Long, lastModified: Long, duration: Long) {
    Row(modifier = Modifier.fillMaxWidth()) {
        AudioDurationText(duration, modifier = Modifier.weight(1f))
        SizeAndLastModifiedText(size, lastModified)
    }
}

@Composable
private fun AudioFileSubInfoItemTwoRow(
    size: Long,
    lastModified: Long,
    duration: Long,
    contentPosition: Long
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            AudioDurationText(duration, modifier = Modifier.weight(1f))
            SizeAndLastModifiedText(size, lastModified)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AudioDurationText(contentPosition)
            LinearProgressIndicator(
                progress = { contentPosition.toFloat() / duration },
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minWidth = 0.dp)
                    .fillMaxWidth()
                    .padding(start = 10.dp),
                gapSize = 0.dp,
                drawStopIndicator = {}
            )
        }
    }
}