package com.hashsoft.audiotape.ui

import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import com.hashsoft.audiotape.data.AudioTapeDto

class SharedViewModel() : ViewModel() {

    private var _audioTape: AudioTapeDto = AudioTapeDto("", "", 0)

    val audioTape: AudioTapeDto
        get() = _audioTape

    fun setAudioTape(audioTape: AudioTapeDto) {
        _audioTape = audioTape
    }


}

val LocalSharedViewModel =
    compositionLocalOf<SharedViewModel> { error("SharedViewModel not found !") }