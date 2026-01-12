package com.hashsoft.audiotape.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import com.hashsoft.audiotape.data.ItemStatus


val smallFontSize: TextUnit
    @Composable @ReadOnlyComposable get() = MaterialTheme.typography.bodySmall.fontSize

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
fun libraryColor(status: ItemStatus, defaultColor: Color): Color {
    return when (status) {
        ItemStatus.Warning, ItemStatus.Missing -> warningItemColor

        else -> defaultColor
    }
}

fun libraryAlpha(status: ItemStatus): Float {
    return when (status) {
        ItemStatus.Disabled, ItemStatus.Missing -> 0.38f

        else -> 1.0f
    }
}

@Composable
fun simpleAudioPlayContentColor(status: ItemStatus): Color {
    return when (status) {
        ItemStatus.Normal -> MaterialTheme.colorScheme.onTertiary

        else -> MaterialTheme.colorScheme.onTertiaryContainer
    }
}

@Composable
fun simpleAudioPlayBackgroundColor(status: ItemStatus): Color {
    return when (status) {
        ItemStatus.Normal -> MaterialTheme.colorScheme.tertiary

        else -> MaterialTheme.colorScheme.tertiaryContainer
    }
}

@Composable
fun simpleAudioPlayBorderColor(status: ItemStatus): Color =
    simpleAudioPlayBackgroundColor(status).copy(alpha = 0.7f)

@Composable
fun simpleAudioPlayIndicatorColor(status: ItemStatus): Color {
    return when (status) {
        ItemStatus.Normal -> MaterialTheme.colorScheme.onTertiary

        else -> MaterialTheme.colorScheme.tertiary
    }
}

@Composable
fun simpleAudioPlayIndicatorTrackColor(status: ItemStatus): Color {
    return when (status) {
        ItemStatus.Normal -> MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.12f)

        else -> MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.12f)
    }
}