/**
 * 権限管理に関する機能を提供するパッケージ
 */
package com.hashsoft.audiotape

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.hashsoft.audiotape.ui.PermissionViewModel
import com.hashsoft.audiotape.ui.dialog.PermissionRationaleDialog

/**
 * アプリの実行に必要な権限（ストレージアクセス）を要求・確認するコンポーザブル
 *
 * 権限が許可されている場合はメインコンテンツ（RouteContent）を表示し、
 * 許可されていない場合は権限リクエストまたは設定画面への誘導を行う
 *
 * @param viewModel 権限状態の登録や解放を管理するViewModel
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Permission(viewModel: PermissionViewModel = hiltViewModel()) {
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
            // 2回目以降の時のためにReleaseする
            viewModel.release()
            viewModel.register()
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

/**
 * 権限が拒否された場合に、アプリの詳細設定画面を開くためのダイアログを表示する
 */
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
