package com.hashsoft.audiotape.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit


val smallFontSize: TextUnit
    @Composable @ReadOnlyComposable get() = MaterialTheme.typography.bodySmall.fontSize

val simpleAudioPlayBackgroundColor: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.tertiary

val simpleAudioPlayBorderColor: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)

val simpleAudioPlayContentColor: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onTertiary

val currentItemBackgroundColor: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceVariant

val currentItemContentColor: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurfaceVariant

@Composable
fun resolveColorForState(color: Color, state: Int): Color {
    return when (state) {
        1 -> color.copy(alpha = color.alpha + 0.5f)
        2 -> color.copy(red = color.red + 0.8f, green = color.green + 0.2f)
        3 -> color.copy(
            alpha = color.alpha + 0.5f,
            red = color.red + 0.8f,
            green = color.green + 0.2f
        )

        else -> color
    }
}