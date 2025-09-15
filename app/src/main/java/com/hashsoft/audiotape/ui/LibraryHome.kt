package com.hashsoft.audiotape.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LifecycleStartEffect
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.ui.bar.TopBar
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryHomeRoute(controller: AudioController = AudioController()) {
    val context = LocalContext.current
    LifecycleStartEffect(controller) {
        controller.buildController(context)
        onStopOrDispose {
            controller.releaseController()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(
                stringResource(R.string.app_name), TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.outlineVariant,
                )
            )
        }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            LibrarySheetRoute(controller)
        }
    }
}