package com.hashsoft.audiotape


import com.hashsoft.audiotape.ui.AboutView
import com.hashsoft.audiotape.ui.LicenseView
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hashsoft.audiotape.ui.AudioPlayHomeRoute
import com.hashsoft.audiotape.ui.LibraryHomeRoute
import com.hashsoft.audiotape.ui.RouteContentViewModel
import com.hashsoft.audiotape.ui.RouteStateUiState
import com.hashsoft.audiotape.ui.UserSettingsHomeRoute

/**
 * アプリケーションのメインコンテンツとなるルートを定義する
 *
 * @param context コンテキスト
 * @param viewModel ルートの状態を管理するViewModel
 */
@Composable
fun RouteContent(viewModel: RouteContentViewModel = hiltViewModel()) {
    when (val uiState = viewModel.uiState.value) {
        is RouteStateUiState.Loading -> {}
        is RouteStateUiState.Success -> {
            RouteScreen(uiState.routeState.startScreen) {
                viewModel.saveStartScreen(it)
            }
        }
    }


}

/**
 * ナビゲーションのホストとなる画面
 *
 * @param startScreen 開始画面のルート名
 * @param onChangeStartScreen 開始画面が変更されたときに呼び出されるコールバック
 */
@Composable
private fun RouteScreen(
    startScreen: String,
    onChangeStartScreen: (String) -> Unit = {}
) {
    val navController = rememberNavController()
    val start = stringToRoute(startScreen)
    val currentBackStack by navController.currentBackStackEntryAsState()
    val audioPlayRoute = currentBackStack?.destination?.hasRoute(Route.AudioPlay::class) ?: false

    NavHost(
        navController = navController,
        startDestination = start,
    ) {
        composable<Route.Library> {
            LibraryHomeRoute {
                navController.navigate(it)
            }
        }
        composable<Route.AudioPlay>(
            enterTransition = { slideInVertically(initialOffsetY = { it }) },
            popExitTransition = { slideOutVertically(targetOffsetY = { it }) }
        ) {
            AudioPlayHomeRoute {
                popAndNavigateLibrary(navController)
            }
        }
        composable<Route.UserSettings> {
            UserSettingsHomeRoute {
                popAndNavigateLibrary(navController)
            }
        }
        composable<Route.About> {
            AboutView(onTransferClick = { navController.navigate(it) }) {
                popAndNavigateLibrary(navController)
            }
        }

        composable<Route.License> {
            LicenseView {
                popAndNavigateLibrary(navController)
            }
        }
    }

    RouteBackProcess(audioPlayRoute) {
        popAndNavigateLibrary(navController)
    }
    onChangeStartScreen(currentBackStack?.destination?.route ?: "")
}

/**
 * ルート名を実際のルートオブジェクトに変換する
 *
 * @param routeName ルート名
 * @return 対応するルートオブジェクト。見つからない場合は[Route.Library]を返す
 */
private fun stringToRoute(routeName: String): Any {
    return when (routeName) {
        Route.Library::class.qualifiedName -> Route.Library
        Route.AudioPlay::class.qualifiedName -> Route.AudioPlay
        else -> Route.Library

    }
}

/**
 * バックスタックをポップし、スタックが空の場合はライブラリ画面に遷移する
 *
 * @param navController ナビゲーションコントローラー
 */
private fun popAndNavigateLibrary(
    navController: NavHostController
) {
    if (!navController.popBackStack()) {
        navController.navigate(Route.Library)
    }
}
