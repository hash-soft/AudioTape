package com.hashsoft.audiotape


import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.ui.AudioPlayHomeRoute
import com.hashsoft.audiotape.ui.LibraryHomeRoute
import com.hashsoft.audiotape.ui.UserSettingsRoute

@Composable
fun RouteContent() {
//    val audioController = viewModel {
//        val application = get(APPLICATION_KEY) as AudioTape
//        AudioControllerViewModel(
//            application,
//            _playbackRepository = application.playbackRepository,
//            _playingStateRepository = application.playingStateRepository,
//            _storageItemListRepository = StorageItemListRepository(application),
//            _audioTapeRepository = application.databaseContainer.audioTapeRepository
//        )
//    }

    //val isUsable by audioController.isUsable
    //if (isUsable) {
    val navController = rememberNavController()
    RouteScreen(navController/*, audioController*/)
    //}
}

val LocalNavController =
    compositionLocalOf<NavHostController> { error("NavHostController not found !") }

@Composable
private fun RouteScreen(
    navController: NavHostController,
    //audioController: AudioControllerViewModel,
) {
    CompositionLocalProvider(
        LocalNavController provides navController,
        //LocalAudioControllerViewModel provides audioController
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
                AudioPlayHomeRoute(AudioTapeDto(audioPlay.folderPath, audioPlay.currentName, 0))
            }
            composable<Route.UserSettings> {
                UserSettingsRoute()
            }
        }
    }
}
