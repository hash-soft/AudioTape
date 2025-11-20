package com.hashsoft.audiotape.data


data class DisplayFolder(
    val folderPath: String,
    val list: List<StorageItem> = emptyList(),
    val expandIndexList: List<Int> = emptyList(),
    val audioTape: AudioTapeDto = AudioTapeDto("", ""),
    val playingState: PlayingStateDto = PlayingStateDto(""),
    val controllerState: ControllerState = ControllerState(false, false)
)
