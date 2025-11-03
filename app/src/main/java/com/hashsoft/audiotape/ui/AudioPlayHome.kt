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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hashsoft.audiotape.R
import com.hashsoft.audiotape.data.AudioItemDto
import com.hashsoft.audiotape.data.AudioTapeDto
import com.hashsoft.audiotape.data.PlayAudioDto


/**
 * オーディオ再生画面のルート
 *
 * @param viewModel ViewModel
 * @param onBackClick 戻るボタンクリック時のコールバック
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayHomeRoute(
    viewModel: AudioPlayViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val playItem by viewModel.playItemState.item.collectAsStateWithLifecycle()
    val playList by viewModel.playListState.list.collectAsStateWithLifecycle()
    val contentPosition by viewModel.contentPosition.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        playItem?.audioTape?.folderPath ?: stringResource(R.string.loading)
                    )
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
            AudioPlayHome(contentPosition = contentPosition, playItem, playList, { argument ->
                audioPlay(argument = argument, viewModel = viewModel)
            }) { argument ->
                val tape = playItem?.audioTape ?: return@AudioPlayHome
                tapeSettings(argument, tape, viewModel)
            }
        }
    }
}

/**
 * オーディオ再生画面のホーム
 *
 * @param playItem 再生アイテム
 * @param playList 再生リスト
 * @param onAudioItemClick オーディオ項目クリック時のコールバック
 * @param onChangeTapeSettings テープ設定変更時のコールバック
 */
@Composable
private fun AudioPlayHome(
    contentPosition: Long,
    playItem: PlayAudioDto?,
    playList: List<AudioItemDto>,
    onAudioItemClick: (AudioCallbackArgument) -> Unit = {},
    onChangeTapeSettings: (TapeSettingsCallbackArgument) -> Unit = {}
) {
    // Todo 再生画面でテープがありませんが一瞬表示されるが見せたくない nullだけではだめ
    if (playItem == null) {
        NoTapeView()
    } else {
        AudioPlayView(
            contentPosition = contentPosition,
            playItem,
            playList,
            onAudioItemClick,
            onChangeTapeSettings
        )
    }
}

/**
 * オーディオ再生処理
 *
 * @param argument オーディオコールバックの引数
 * @param viewModel ViewModel
 */
private fun audioPlay(argument: AudioCallbackArgument, viewModel: AudioPlayViewModel) {
    when (argument) {
        is AudioCallbackArgument.AudioSelected -> {
            viewModel.setMediaItemsInAudioList(argument.index)
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
                viewModel.setPlayingParameters()
                viewModel.play()
                // 最終再生更新
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
            viewModel.sortList(argument.sortOrder)
        }
    }
}
