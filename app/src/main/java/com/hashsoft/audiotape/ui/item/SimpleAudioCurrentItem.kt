package com.hashsoft.audiotape.ui.item

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.hashsoft.audiotape.ui.text.AudioDurationText
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme
import com.hashsoft.audiotape.ui.theme.ListLabelSpace


@Composable
fun AdaptiveSimpleAudioCurrentItem(
    name: String,
    directory: String,
    contentPosition: Long,
    modifier: Modifier = Modifier
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    if (isLandscape) {
        SimpleAudioCurrentItemLandScope(name, directory, contentPosition, modifier)
    } else {
        SimpleAudioCurrentItem(name, directory, contentPosition, modifier)
    }
}

@Composable
fun SimpleAudioCurrentItem(
    name: String,
    directory: String,
    contentPosition: Long,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ListLabelSpace),
        ) {
            Text(
                directory,
                modifier = Modifier
                    .weight(1f),
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                maxLines = 1,
                overflow = TextOverflow.StartEllipsis,
            )
            AudioDurationText(
                duration = contentPosition,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
            )
        }
        Text(
            text = name,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }

}

@Composable
fun SimpleAudioCurrentItemLandScope(
    name: String,
    directory: String,
    contentPosition: Long,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ListLabelSpace),
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(ListLabelSpace)
        ) {
            Text(
                directory,
                modifier = Modifier.weight(1f, fill = false),
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                maxLines = 1,
                overflow = TextOverflow.StartEllipsis,
            )
            Text(
                text = name,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        AudioDurationText(
            duration = contentPosition,
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun SimpleAudioCurrentItemPreview() {
    AudioTapeTheme {
        SimpleAudioCurrentItem(
            name = "name",
            directory = "テープ名000000000000000000000000000000",
            contentPosition = 500
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SimpleAudioCurrentItemLandScopePreview() {
    AudioTapeTheme {
        SimpleAudioCurrentItemLandScope(
            name = "name00",
            directory = "テープ名000000000000000000000000000000",
            contentPosition = 500
        )
    }
}