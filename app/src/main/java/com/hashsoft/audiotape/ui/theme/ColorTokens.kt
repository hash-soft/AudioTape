package com.hashsoft.audiotape.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import com.hashsoft.audiotape.data.ItemStatus


val smallFontSize: TextUnit
    @Composable @ReadOnlyComposable get() = MaterialTheme.typography.bodySmall.fontSize

val simpleAudioPlayBackgroundColor: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.tertiary

val simpleAudioPlayBorderColor: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)

val simpleAudioPlayContentColor: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onTertiary

val simpleAudioPlayIndicatorColor: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onTertiary

val simpleAudioPlayIndicatorTrackColor: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onTertiaryContainer

val currentItemBackgroundColor: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.secondaryFixed

val currentItemContentColor: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSecondaryFixed

val addressBarBackgroundColor: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceVariant

val addressBarContentColor: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurfaceVariant

val primaryItemColor: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary

val warningItemColor: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.error

val defaultSurfaceContentColor: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurfaceVariant


@Composable
fun resolveColorForState(status: ItemStatus, defaultColor: Color): Color {
    return when (status) {
        ItemStatus.Warning, ItemStatus.Missing -> warningItemColor

        else -> defaultColor
    }
}

fun resolveAlphaForState(status: ItemStatus): Float {
    return when (status) {
        ItemStatus.Disabled, ItemStatus.Missing -> 0.38f

        else -> 1.0f
    }
}
