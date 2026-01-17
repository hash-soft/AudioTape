package com.hashsoft.audiotape.ui.animation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hashsoft.audiotape.ui.theme.EqualizerSize
import com.hashsoft.audiotape.ui.theme.currentItemContentColor

@Composable
fun EqualizerAnimation() {
    val transition = rememberInfiniteTransition(label = "equalizer")

    val heights = listOf(0.4f, 0.8f, 0.5f).mapIndexed { index, initial ->
        transition.animateFloat(
            initialValue = initial,
            targetValue = 1f - initial,
            animationSpec = infiniteRepeatable(
                animation = tween(500 + (index * 100), easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar$index"
        )
    }

    Row(
        modifier = Modifier.size(EqualizerSize),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        heights.forEach { height ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(height.value)
                    .background(currentItemContentColor, shape = RoundedCornerShape(1.dp))
            )
        }
    }
}
