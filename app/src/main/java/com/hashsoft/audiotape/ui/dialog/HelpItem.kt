package com.hashsoft.audiotape.ui.dialog

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class HelpItem(
    val icon: ImageVector,
    @param:StringRes val titleRes: Int,
    @param:StringRes val descRes: Int
)
