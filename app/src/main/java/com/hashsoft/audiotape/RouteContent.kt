package com.hashsoft.audiotape


import android.content.Context
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
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
import com.hashsoft.audiotape.ui.UserSettingsRoute
import kotlinx.coroutines.launch

/**
 * アプリケーションのメインコンテンツとなるルートを定義する
 *
 * @param viewModel ルートの状態を管理するViewModel
 */
@Composable
fun RouteContent(
    context: Context = LocalContext.current,
    viewModel: RouteContentViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    LifecycleStartEffect(Unit) {
        coroutineScope.launch {
            viewModel.buildController(context) // suspend関数OK
        }
        onStopOrDispose {
            viewModel.releaseController()
        }
    }

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
                navController.navigate(Route.AudioPlay)
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
            UserSettingsRoute()
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
 * @return 対応するルートオブジェクト。見つからない場合は[Route.Library]
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
