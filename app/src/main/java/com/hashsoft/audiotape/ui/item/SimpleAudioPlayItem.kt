package com.hashsoft.audiotape.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.hashsoft.audiotape.ui.AudioCallbackArgument
import com.hashsoft.audiotape.ui.AudioCallbackResult
import timber.log.Timber

@Composable
fun SimpleAudioPlayItem(
    path: String,
    isReadyOk: Boolean = true,
    isPlaying: Boolean = false,
    durationMs: Long = 0,
    contentPosition: Long = 0,
    audioCallback: (AudioCallbackArgument) -> AudioCallbackResult = { AudioCallbackResult.None }
) {
    var isSwipingStage by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .clickable(onClick = {
                // タップ効果だけのために設定する
            })
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitFirstDown()
                        var accumulatedDeltaY = 0f
                        isSwipingStage = 1
                        do {
                            val event = awaitPointerEvent()
                            val activeChange = event.changes.firstOrNull { it.pressed }
                            if (activeChange != null && activeChange.pressed) {
                                accumulatedDeltaY += activeChange.previousPosition.y - activeChange.position.y
                                isSwipingStage = if(accumulatedDeltaY > 50f )  2 else 1
                            } else {
                                // accumulatedDeltaYは整数には丸める
                                if(isSwipingStage == 2 || accumulatedDeltaY == 0.0f){
                                    audioCallback(AudioCallbackArgument.OpenAudioPlay)
                                }
                                isSwipingStage = 0
                                break
                            }
                        } while (true)
                    }
                }
            }
    ) {
        if (isSwipingStage > 0) {
            Text(
                text = if(isSwipingStage == 2) "演奏画面に遷移" else "上にスワイプで演奏画面に遷移"
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(top = 8.dp, bottom = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            HorizontalDivider(
                modifier = Modifier
                    .width(40.dp) // Slightly wider for better touch target
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = 0.4f
                )
            )
        }

        SimpleAudioPlayContent(
            path,
            isReadyOk,
            isPlaying,
            durationMs,
            contentPosition,
            audioCallback
        )
    }
}
