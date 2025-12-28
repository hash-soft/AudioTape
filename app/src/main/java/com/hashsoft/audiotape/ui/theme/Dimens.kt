package com.hashsoft.audiotape.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

/**
 * 中サイズのアイコン
 */
val IconMedium = 36.dp

val SettingItemHorizontalPadding = 16.dp

val SettingItemVerticalPadding = 12.dp

val SettingItemVerticalDistance = 10.dp

val SettingNextLabelVerticalAdd = 4.dp

val DialogCornerRadius = 16.dp

val DialogHorizontalPadding = 20.dp

val DialogVerticalPadding = 18.dp

val DialogTitleBottomPadding = 12.dp

val ListLabelSpace = 8.dp

val smallFontSize: TextUnit
    @Composable @ReadOnlyComposable get() = MaterialTheme.typography.bodySmall.fontSize
