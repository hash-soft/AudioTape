package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.ui.theme.ListLabelSpace


@Composable
fun CurrentAudioNameItem(
    index: Int,
    total: Int,
    name: String,
    fontSize: TextUnit = TextUnit.Unspecified,
    style: TextStyle = LocalTextStyle.current
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(ListLabelSpace),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.audio_item_progress_label, index, total),
            fontSize = fontSize,
            style = style
        )
        Text(
            text = name, fontSize = fontSize, style = style, maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

}
