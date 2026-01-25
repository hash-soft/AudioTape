package com.hashsoft.audiotape.ui.text

import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.hashsoft.audiotape.ui.theme.AudioTapeTheme

@Composable
fun FixedWidthText(
    text: String,
    maxChars: Int,
    modifier: Modifier = Modifier,
    sampleChar: String = "W",
    textAlign: TextAlign = TextAlign.Center,
    style: TextStyle = LocalTextStyle.current
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    val maxWidthDp = remember(maxChars, style, density, sampleChar) {
        val worstCaseString = sampleChar.repeat(maxChars)
        val result = textMeasurer.measure(
            text = worstCaseString,
            style = style,
            maxLines = 1
        )
        with(density) { result.size.width.toDp() }
    }

    Text(
        text = text,
        style = style,
        modifier = modifier.width(maxWidthDp),
        textAlign = textAlign,
        maxLines = 1
    )
}


@Preview(showBackground = true)
@Composable
fun FixedWidthTextPreview() {
    AudioTapeTheme {
        FixedWidthText("あいうえお", 5, sampleChar = "あ")
    }
}