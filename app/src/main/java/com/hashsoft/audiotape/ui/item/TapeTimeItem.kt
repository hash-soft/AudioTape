package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.logic.TimeFormat.Companion.formatDateTimeHm
import java.time.format.FormatStyle


@Composable
fun TapeTimeItem(
    lastPlayedAt: Long,
    createdTime: Long,
    fontSize: TextUnit = TextUnit.Unspecified,
    style: TextStyle = LocalTextStyle.current
) {
    Column {
        Text(
            text = stringResource(R.string.last_played_label, formatDateTimeHm(lastPlayedAt, FormatStyle.MEDIUM)),
            fontSize = fontSize,
            style = style
        )
        Text(
            text = stringResource(R.string.created_label, formatDateTimeHm(createdTime, FormatStyle.MEDIUM)),
            fontSize = fontSize,
            style = style
        )
    }

}