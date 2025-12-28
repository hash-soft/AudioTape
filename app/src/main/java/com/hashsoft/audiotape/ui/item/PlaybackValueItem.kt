package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import com.hashsoft.audiotape.ui.resource.displayPitchValue
import com.hashsoft.audiotape.ui.resource.displaySpeedValue
import com.hashsoft.audiotape.ui.resource.displayVolumeValue


@Composable
fun PlaybackValueItem(
    volume: Float,
    speed: Float,
    pitch: Float,
    isRepeat: Boolean,
    fontSize: TextUnit = TextUnit.Unspecified,
    style: TextStyle = LocalTextStyle.current
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = displayVolumeValue(volume).second, fontSize = fontSize, style = style)
        Text(text = displaySpeedValue(speed).second, fontSize = fontSize, style = style)
        Text(text = displayPitchValue(pitch).second, fontSize = fontSize, style = style)
        if (isRepeat) {
            Text(text = "Repeat", fontSize = fontSize, style = style)
        }
    }

}
