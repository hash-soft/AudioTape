package com.hashsoft.audiotape.ui.item

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.logic.TimeFormat.Companion.formatDateTimeHm
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme
import java.time.format.FormatStyle


@Composable
fun AdaptiveTapeTimeItem(
    lastPlayedAt: Long,
    createdTime: Long,
    style: TextStyle = LocalTextStyle.current
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    if (isLandscape) {
        TapeTimeItemPortrait(lastPlayedAt, createdTime, style)
    } else {
        TapeTimeItem(lastPlayedAt, createdTime, style)
    }
}

@Composable
fun TapeTimeItem(
    lastPlayedAt: Long,
    createdTime: Long,
    style: TextStyle = LocalTextStyle.current
) {
    Column {
        ProvideTextStyle(value = style) {
            Text(
                text = stringResource(
                    R.string.last_played_label,
                    formatDateTimeHm(lastPlayedAt, FormatStyle.MEDIUM)
                ),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                text = stringResource(
                    R.string.created_label,
                    formatDateTimeHm(createdTime, FormatStyle.MEDIUM)
                ),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }

}

@Composable
fun TapeTimeItemPortrait(
    lastPlayedAt: Long,
    createdTime: Long,
    style: TextStyle = LocalTextStyle.current
) {
    Row {
        ProvideTextStyle(value = style) {
            Text(
                text = stringResource(
                    R.string.last_played_label,
                    formatDateTimeHm(lastPlayedAt, FormatStyle.MEDIUM)
                ),
                modifier = Modifier.weight(1f),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                text = stringResource(
                    R.string.created_label,
                    formatDateTimeHm(createdTime, FormatStyle.MEDIUM)
                ),
                modifier = Modifier.weight(1f),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun TapeTimeItemPreview() {
    AudioTapeTheme {
        TapeTimeItem(
            lastPlayedAt = 1000,
            createdTime = 2000,
            style = LocalTextStyle.current
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TapeTimeItemPortraitPreview() {
    AudioTapeTheme {
        TapeTimeItemPortrait(
            lastPlayedAt = 1000,
            createdTime = 2000,
            style = LocalTextStyle.current
        )
    }
}