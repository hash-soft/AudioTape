package com.hashsoft.audiotape.ui.di.module

import android.content.Context
import com.hashsoft.audiotape.data.AudioItemListRepository
import com.hashsoft.audiotape.data.PlaybackRepository
import com.hashsoft.audiotape.data.ResumeAudioRepository
import com.hashsoft.audiotape.data.StorageAddressRepository
import com.hashsoft.audiotape.data.StorageItemListRepository
import com.hashsoft.audiotape.ui.AudioController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAudioController(): AudioController = AudioController()

    @Provides
    fun provideStorageAddressRepository(@ApplicationContext context: Context): StorageAddressRepository {
        return StorageAddressRepository(context)
    }

    @Provides
    fun provideStorageItemListRepository(@ApplicationContext context: Context): StorageItemListRepository {
        return StorageItemListRepository(context)
    }

    @Provides
    @Singleton
    fun providePlaybackRepository(): PlaybackRepository = PlaybackRepository()

    @Provides
    @Singleton
    fun provideResumeAudioRepository(): ResumeAudioRepository = ResumeAudioRepository()

    @Provides
    fun provideAudioItemListRepository(@ApplicationContext context: Context): AudioItemListRepository {
        return AudioItemListRepository(context)
    }

}
