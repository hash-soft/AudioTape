package com.hashsoft.audiotape.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.Route
import com.hashsoft.audiotape.ui.dropdown.TextDropdownSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryHomeRoute(onTransferClick: (route: Any) -> Unit = {}) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    MenuDropdownSelector {
                        when (it) {
                            0 -> onTransferClick(Route.UserSettings)
                            else -> {}
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.outlineVariant,
                )
            )
        }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            LibrarySheetRoute {
                onTransferClick(Route.AudioPlay)
            }
        }
    }
}

@Composable
private fun MenuDropdownSelector(
    onChange: (Int) -> Unit = {}
) {
    val sortLabels = stringArrayResource(R.array.menu_labels).toList()

    TextDropdownSelector(sortLabels, "", selectedIndex = -1, iconContent = {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = stringResource(R.string.menu_description),
        )
    }) {
        onChange(it)
    }
}
