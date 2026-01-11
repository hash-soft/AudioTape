package com.hashsoft.audiotape.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

/**
 * 日本語と英数字の混在による高さのズレを防ぐため、
 * 全てのスタイルで [android.R.attr.includeFontPadding] を true に設定したTypography定義。
 */
private val defaultPlatformStyle = PlatformTextStyle(
    includeFontPadding = true
)

// 各スタイルのベースとして使用
private val baseTextStyle = TextStyle(
    platformStyle = defaultPlatformStyle
)

val AppTypography = Typography().run {
    copy(
        displayLarge = displayLarge.copy(platformStyle = defaultPlatformStyle),
        displayMedium = displayMedium.copy(platformStyle = defaultPlatformStyle),
        displaySmall = displaySmall.copy(platformStyle = defaultPlatformStyle),
        headlineLarge = headlineLarge.copy(platformStyle = defaultPlatformStyle),
        headlineMedium = headlineMedium.copy(platformStyle = defaultPlatformStyle),
        headlineSmall = headlineSmall.copy(platformStyle = defaultPlatformStyle),
        titleLarge = titleLarge.copy(platformStyle = defaultPlatformStyle),
        titleMedium = titleMedium.copy(platformStyle = defaultPlatformStyle),
        titleSmall = titleSmall.copy(platformStyle = defaultPlatformStyle),
        bodyLarge = bodyLarge.copy(platformStyle = defaultPlatformStyle),
        bodyMedium = bodyMedium.copy(platformStyle = defaultPlatformStyle),
        bodySmall = bodySmall.copy(platformStyle = defaultPlatformStyle),
        labelLarge = labelLarge.copy(platformStyle = defaultPlatformStyle),
        labelMedium = labelMedium.copy(platformStyle = defaultPlatformStyle),
        labelSmall = labelSmall.copy(platformStyle = defaultPlatformStyle)
    )
}