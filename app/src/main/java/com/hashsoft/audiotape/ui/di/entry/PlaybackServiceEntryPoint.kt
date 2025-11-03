package com.hashsoft.audiotape.ui.di.entry

import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.ContentPositionRepository
import com.hashsoft.audiotape.data.PlaybackRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PlaybackServiceEntryPoint {
    fun playbackRepository(): PlaybackRepository
    fun contentPositionRepository(): ContentPositionRepository
    fun audioTapeRepository(): AudioTapeRepository
    fun audioStoreRepository(): AudioStoreRepository
    fun playingStateRepository(): PlayingStateRepository
}
