package com.hashsoft.audiotape.ui.di.entry

import com.hashsoft.audiotape.data.AudioItemListRepository
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.PlaybackRepository
import com.hashsoft.audiotape.data.ResumeAudioRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PlaybackServiceEntryPoint {
    fun playbackRepository(): PlaybackRepository
    fun audioTapeRepository(): AudioTapeRepository
    fun resumeAudioRepository(): ResumeAudioRepository
    fun audioItemListRepository(): AudioItemListRepository
}
