package com.hashsoft.audiotape


import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.hashsoft.audiotape.ui.LibraryHomeRoute
import com.hashsoft.audiotape.ui.RouteContentViewModel
import com.hashsoft.audiotape.ui.UserSettingsRoute

@Composable
fun RouteContent(viewModel: RouteContentViewModel = hiltViewModel()) {
    val context = LocalContext.current
    LifecycleStartEffect(Unit) {
        viewModel.buildController(context)
        onStopOrDispose {
            viewModel.releaseController()
        }
    }

    val navController = rememberNavController()
    RouteScreen(navController)
}

val LocalNavController =
    compositionLocalOf<NavHostController> { error("NavHostController not found !") }

@Composable
private fun RouteScreen(navController: NavHostController) {
    CompositionLocalProvider(
        LocalNavController provides navController,
    ) {
        val start = Route.Library
        NavHost(
            navController = navController,
            startDestination = start,
        ) {
            composable<Route.Library> {
                LibraryHomeRoute()
            }
            composable<Route.AudioPlay>(
                enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
            ) { navBackStackEntry ->
                val audioPlay: Route.AudioPlay = navBackStackEntry.toRoute()
            }
            composable<Route.UserSettings> {
                UserSettingsRoute()
            }
        }
    }
}
