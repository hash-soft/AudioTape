package com.hashsoft.audiotape.ui

import com.hashsoft.audiotape.data.AudioItemMetadata

sealed interface AudioCallbackArgument {
    data class Display(val index: Int) : AudioCallbackArgument
    data class PlayPause(
        val isPlaying: Boolean
    ) : AudioCallbackArgument

    data class FolderSelected(val path: String) : AudioCallbackArgument
    data class AudioSelected(
        val index : Int
    ) : AudioCallbackArgument

    data object Position : AudioCallbackArgument
    data class SeekTo(
        val position: Long
    ) : AudioCallbackArgument

}