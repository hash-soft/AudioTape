package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.logic.TimeFormat.Companion.formatMillis
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme
import com.hashsoft.audiotape.ui.theme.ListLabelSpace


@Composable
fun CurrentAudioNameItem(
    no: Int,
    count: Int,
    position: Long,
    name: String,
    style: TextStyle = LocalTextStyle.current
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(ListLabelSpace),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProvideTextStyle(value = style) {
            Text(
                text = stringResource(
                    R.string.audio_item_progress_label,
                    if (no <= 0) stringResource(R.string.audio_item_unknow_no) else no.toString(),
                    count
                ),
                modifier = Modifier.alignByBaseline()
            )
            Text(
                text = formatMillis(position),
            )
            Text(
                text = name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.alignByBaseline()
            )
        }
    }

}


@Preview(showBackground = true)
@Composable
fun CurrentAudioNameItemPreview() {
    AudioTapeTheme {
        CurrentAudioNameItem(
            0,
            10,
            position = 11200,
            "みえないつばさ",
            style = LocalTextStyle.current
        )
    }
}