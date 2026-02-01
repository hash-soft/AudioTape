package com.hashsoft.audiotape.ui.item

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.data.AudioTapeSortOrder
import com.hashsoft.audiotape.ui.resource.buildAnnotatedSettings
import com.hashsoft.audiotape.ui.resource.displayPitchValue
import com.hashsoft.audiotape.ui.resource.displaySortOrderValue
import com.hashsoft.audiotape.ui.resource.displaySpeedValue
import com.hashsoft.audiotape.ui.resource.displayVolumeValue
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme


@Composable
fun PlaybackValueItem(
    volume: Float,
    speed: Float,
    pitch: Float,
    isRepeat: Boolean,
    sortOrder: AudioTapeSortOrder,
    style: TextStyle = LocalTextStyle.current
) {
    val text = buildAnnotatedSettings(
        stringResource(R.string.tape_settings_separator),
        displayVolumeValue(volume).second,
        displaySpeedValue(speed).second,
        displayPitchValue(pitch).second,
        if (isRepeat) stringResource(R.string.repeat_label) else "",
        displaySortOrderValue(sortOrder.ordinal)
    )
    Text(text = text, style = style, overflow = TextOverflow.Ellipsis, maxLines = 1)
}


@Preview(showBackground = true)
@Composable
fun PlaybackValueItemPreview() {
    AudioTapeTheme {
        PlaybackValueItem(
            volume = 1.0f,
            speed = 1.0f,
            pitch = 1.0f,
            isRepeat = false,
            sortOrder = AudioTapeSortOrder.ARTIST_ASC,
            style = LocalTextStyle.current
        )
    }
}