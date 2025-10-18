package com.hashsoft.audiotape.ui

sealed interface AudioCallbackArgument {
    // 再生項目用

    data class PlayPause(
        val isPlaying: Boolean
    ) : AudioCallbackArgument

    data object Position : AudioCallbackArgument

    data class SeekTo(
        val position: Long
    ) : AudioCallbackArgument

    data object SkipNext : AudioCallbackArgument
    data object SkipPrevious : AudioCallbackArgument
    data object TransferAudioPlay: AudioCallbackArgument


    // フォルダ選択用

    data class AudioSelected(
        val index: Int
    ) : AudioCallbackArgument

    data class FolderSelected(val path: String) : AudioCallbackArgument


    // テープ一覧用
    data class TapeSelected(val index: Int) : AudioCallbackArgument
    data class TapeFolderOpen(val path: String) : AudioCallbackArgument


}