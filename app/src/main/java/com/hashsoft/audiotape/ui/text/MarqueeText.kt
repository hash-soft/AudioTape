package com.hashsoft.audiotape.ui.text

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun TappableMarqueeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    initialDelayMillis: Int = 500,
    velocity: Int = 50
) {
    var playCount by remember { mutableIntStateOf(0) }

    key(playCount) {
        Text(
            text = text,
            modifier = modifier
                .clickable { playCount++ }
                .basicMarquee(
                    iterations = 1,
                    initialDelayMillis = initialDelayMillis,
                    velocity = velocity.dp
                ),
            color = color,
            style = style
        )
    }
}