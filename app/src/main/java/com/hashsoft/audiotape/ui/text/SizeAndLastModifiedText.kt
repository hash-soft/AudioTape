package com.hashsoft.audiotape.ui.text

import android.text.format.Formatter
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import com.hashsoft.audiotape.logic.TimeFormat

@Composable
fun SizeAndLastModifiedText(
    size: Long,
    lastModified: Long,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
) {
    val context = LocalContext.current
    Text(
        text = "${
            Formatter.formatFileSize(
                context,
                size
            )
        } ${
            TimeFormat.formatDateTimeHm(
                lastModified
            )
        }",
        modifier = modifier,
        style = style
    )
}