package com.hashsoft.audiotape.ui.sheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset

@Composable
fun SimpleBottomSheet(
    isEnterAnimated: Boolean,
    isExitAnimated: Boolean,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    sheetContent: @Composable () -> Unit,
    color: Color = MaterialTheme.colorScheme.surface
) {
    val state = remember {
        MutableTransitionState(false).apply {
            // Start the animation immediately.
            targetState = true
        }
    }

    LaunchedEffect(state.isIdle, state.currentState) {
        if (state.isIdle && !state.currentState) {
            onDismissRequest()
        }
    }

    val enterAnimationDuration = if (isEnterAnimated) 300 else 0
    val enterSlideInSpec = tween<IntOffset>(durationMillis = enterAnimationDuration)
    val enterFadeSpec = tween<Float>(durationMillis = enterAnimationDuration)
    val exitAnimationDuration = if (isExitAnimated) 300 else 0
    val exitSlideInSpec = tween<IntOffset>(durationMillis = exitAnimationDuration)
    val exitFadeSpec = tween<Float>(durationMillis = exitAnimationDuration)

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visibleState = state,
            // アニメーションを無効化
            enter = slideInVertically(animationSpec = enterSlideInSpec) { it } + fadeIn(
                enterFadeSpec
            ),
            exit = slideOutVertically(animationSpec = exitSlideInSpec) { it } + fadeOut(
                exitFadeSpec
            ),

            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clickable(onClick = { state.targetState = false }),
        ) {

            Surface(
                modifier = modifier,
                color = color
            ) {
                sheetContent()
            }
        }
    }

}