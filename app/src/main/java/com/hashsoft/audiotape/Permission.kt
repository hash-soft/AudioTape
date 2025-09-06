package com.hashsoft.audiotape

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.hashsoft.audiotape.ui.dialog.PermissionRationaleDialog

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Permission() {
    val permissionState = rememberPermissionState(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    )
    val status = permissionState.status
    when {
        status.isGranted -> {
            // 許可してないと開始させない
            RouteContent()
        }

        status.shouldShowRationale -> {
            // launchPermissionRequestの2回目の起動以降で表示される
            // 2度と画面を表示しないを選択するとshouldShowRationaleがfalseに戻ってしまうので
            // shouldShowRationaleがtrueになったら
            // launchPermissionRequestを呼ぶ条件に入らないようにする
            LaunchDetailsSettings()
        }

        !status.isGranted && !status.shouldShowRationale -> {
            SideEffect {
                permissionState.launchPermissionRequest()
            }
        }

        else -> {
            LaunchDetailsSettings()
        }
    }
}

@Composable
private fun LaunchDetailsSettings() {
    val context = LocalContext.current
    PermissionRationaleDialog {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }
}