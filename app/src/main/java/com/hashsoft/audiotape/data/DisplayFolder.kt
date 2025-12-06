package com.hashsoft.audiotape.data


data class DisplayFolder(
    val folderPath: String = "",
    val list: List<StorageItem> = emptyList(),
    val expandIndexList: List<Int> = emptyList(),
    val audioTape: AudioTapeDto? = null,
    val playingState: PlayingStateDto = PlayingStateDto(""),
    val settings: UserSettingsDto = UserSettingsDto(UserSettingsRepository.DEFAULT_ID),
    val controllerState: ControllerState = ControllerState(false, false)
)
