package com.hashsoft.audiotape.ui.tab

import android.content.res.Configuration
import androidx.compose.material3.LeadingIconTab
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun AdaptiveTab(
    selected: Boolean,
    onClick: () -> Unit,
    text: @Composable () -> Unit,
    icon: @Composable () -> Unit,
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    if (isLandscape) {
        LeadingIconTab(
            selected = selected,
            onClick = onClick,
            text = text,
            icon = icon

        )
    } else {
        Tab(
            selected = selected,
            onClick = onClick,
            text = text,
            icon = icon
        )
    }
}