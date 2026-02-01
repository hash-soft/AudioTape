package com.hashsoft.audiotape.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.DisplayPlayingSource
import com.hashsoft.audiotape.logic.StorageHelper
import com.hashsoft.audiotape.ui.text.TappableMarqueeText
import com.hashsoft.audiotape.ui.theme.audioPlayContentColor
import com.hashsoft.audiotape.ui.theme.audioPlayTitleAlpha
import com.hashsoft.audiotape.ui.theme.defaultSurfaceContentColor


/**
 * オーディオ再生画面のルート
 *
 * @param viewModel ViewModel
 * @param onBackClick 戻るボタンクリック時のコールバック
 */
@Composable
fun AudioPlayHomeRoute(
    viewModel: AudioPlayViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val displayPlayingSource by viewModel.displayPlayingSource.collectAsStateWithLifecycle()
    val displayPlayingItem by viewModel.displayPlayingState.collectAsStateWithLifecycle()
    val contentPosition by viewModel.currentPositionState.collectAsStateWithLifecycle()
    val available by viewModel.availableState.collectAsStateWithLifecycle()

    when (val result = displayPlayingItem) {
        is PlayItemStateResult.NoTape -> {
            NoTapeView(result.name, onBackClick)
        }

        is PlayItemStateResult.Success -> {
            AudioPlayHome(
                available,
                contentPosition,
                displayPlayingSource,
                result.displayPlayingItem,
                onBackClick = onBackClick,
                { argument ->
                    audioPlay(argument = argument, result.displayPlayingItem, viewModel = viewModel)
                }) { argument ->
                tapeSettings(argument, result.displayPlayingItem.audioTape, viewModel)
            }
        }

        else -> {}
    }


}

/**
 * オーディオ再生画面のホーム
 *
 * @param onAudioItemClick オーディオ項目クリック時のコールバック
 * @param onChangeTapeSettings テープ設定変更時のコールバック
 */
@Composable
private fun AudioPlayHome(
    isAvailable: Boolean,
    contentPosition: Long,
    displayPlaying: DisplayPlayingSource,
    displayPlayingItem: DisplayPlayingItem,
    onBackClick: () -> Unit = {},
    onAudioItemClick: (AudioCallbackArgument) -> Unit = {},
    onChangeTapeSettings: (TapeSettingsCallbackArgument) -> Unit = {}
) {
    var playCount by remember { mutableIntStateOf(0) }
    @OptIn(ExperimentalMaterial3Api::class)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    key(playCount) {
                        TappableMarqueeText(
                            text = StorageHelper.treeListToString(
                                displayPlayingItem.treeList,
                                stringResource(R.string.path_separator),
                                default = displayPlayingItem.audioTape.folderPath
                            ),
                            color = audioPlayContentColor(
                                displayPlayingItem.status,
                                defaultSurfaceContentColor
                            ).copy(alpha = audioPlayTitleAlpha(displayPlayingItem.status))
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onBackClick()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Surface(
                contentColor = audioPlayContentColor(
                    displayPlayingItem.status,
                    defaultSurfaceContentColor
                )
            ) {
                AdaptiveAudioPlayView(
                    isAvailable = isAvailable,
                    contentPosition = contentPosition,
                    tape = displayPlayingItem.audioTape,
                    playList = displayPlayingItem.audioList,
                    status = displayPlayingItem.status,
                    displayPlaying = displayPlaying,
                    onAudioItemClick = onAudioItemClick,
                    onChangeTapeSettings = onChangeTapeSettings
                )
            }
        }
    }
}

/**
 * オーディオ再生処理
 *
 * @param argument オーディオコールバックの引数
 * @param viewModel ViewModel
 */
private fun audioPlay(
    argument: AudioCallbackArgument,
    displayPlayingItem: DisplayPlayingItem?,
    viewModel: AudioPlayViewModel
) {
    if (displayPlayingItem == null) return
    when (argument) {
        is AudioCallbackArgument.AudioSelected -> {
            viewModel.setMediaItemsInAudioList(
                displayPlayingItem.audioList,
                argument.index,
                argument.position
            )
        }

        is AudioCallbackArgument.SkipPrevious -> {
            viewModel.seekToPrevious()
        }

        is AudioCallbackArgument.BackIncrement -> {
            viewModel.seekBack()
        }

        is AudioCallbackArgument.SeekTo -> {
            viewModel.seekTo(argument.position)
        }

        is AudioCallbackArgument.PlayPause -> {
            if (argument.isPlaying) {
                viewModel.pause()
            } else {
                viewModel.setPlayingParameters(displayPlayingItem.audioTape)
                viewModel.playWhenReady(true)
            }
        }

        is AudioCallbackArgument.ForwardIncrement -> {
            viewModel.seekForward()
        }

        is AudioCallbackArgument.SkipNext -> {
            viewModel.seekToNext()
        }

        else -> {}
    }
}

/**
 * テープ設定
 *
 * @param argument テープ設定コールバックの引数
 * @param tape オーディオテープ
 * @param viewModel ViewModel
 */
private fun tapeSettings(
    argument: TapeSettingsCallbackArgument,
    tape: AudioTapeDto,
    viewModel: AudioPlayViewModel
) {
    when (argument) {
        is TapeSettingsCallbackArgument.Volume -> {
            viewModel.updateVolume(tape.folderPath, argument.volume)
            viewModel.setVolume(argument.volume)
        }

        is TapeSettingsCallbackArgument.Speed -> {
            viewModel.updateSpeed(tape.folderPath, argument.speed)
            viewModel.setSpeed(argument.speed)
        }

        is TapeSettingsCallbackArgument.Pitch -> {
            viewModel.updatePitch(tape.folderPath, argument.pitch)
            viewModel.setPitch(argument.pitch)
        }

        is TapeSettingsCallbackArgument.Repeat -> {
            viewModel.updateRepeat(tape.folderPath, argument.repeat)
            viewModel.setRepeat(argument.repeat)
        }

        is TapeSettingsCallbackArgument.SortOrder -> {
            viewModel.updateSortOrder(tape.folderPath, argument.sortOrder)
        }
    }
}
