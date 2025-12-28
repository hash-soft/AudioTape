package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.logic.TimeFormat.Companion.formatDateTimeHm
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme
import java.time.format.FormatStyle


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
            )
            Text(
                text = stringResource(
                    R.string.created_label,
                    formatDateTimeHm(createdTime, FormatStyle.MEDIUM)
                ),
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun TapeTimeItemPreview() {
    AudioTapeTheme {
        TapeTimeItem(
            lastPlayedAt = 1000,
            createdTime = 2000,
            style = LocalTextStyle.current
        )
    }
}