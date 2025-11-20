package com.hashsoft.audiotape.ui.di.entry

import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.AudioTapeStagingRepository
import com.hashsoft.audiotape.data.ContentPositionRepository
import com.hashsoft.audiotape.data.ControllerStateRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PlaybackServiceEntryPoint {
    fun contentPositionRepository(): ContentPositionRepository
    fun audioTapeRepository(): AudioTapeRepository
    fun audioStoreRepository(): AudioStoreRepository
    fun playingStateRepository(): PlayingStateRepository
    fun controllerStateRepository(): ControllerStateRepository
    fun audioTapeStagingRepository(): AudioTapeStagingRepository
}
