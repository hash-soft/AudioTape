package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.hashsoft.audiotape.ui.resource.displayPitchValue
import com.hashsoft.audiotape.ui.resource.displaySpeedValue
import com.hashsoft.audiotape.ui.resource.displayVolumeValue
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme


@Composable
fun PlaybackValueItem(
    volume: Float,
    speed: Float,
    pitch: Float,
    isRepeat: Boolean,
    style: TextStyle = LocalTextStyle.current
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProvideTextStyle(value = style) {
            Text(text = displayVolumeValue(volume).second)
            Text(text = displaySpeedValue(speed).second)
            Text(text = displayPitchValue(pitch).second)
            if (isRepeat) {
                Text(text = "Repeat")
            }
        }
    }

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
            style = LocalTextStyle.current
        )
    }
}