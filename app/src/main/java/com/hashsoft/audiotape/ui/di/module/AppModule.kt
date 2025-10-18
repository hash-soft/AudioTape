package com.hashsoft.audiotape.ui.di.module

import android.content.Context
import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.PlaybackRepository
import com.hashsoft.audiotape.data.ResumeAudioRepository
import com.hashsoft.audiotape.data.StorageAddressUseCase
import com.hashsoft.audiotape.data.StorageItemListUseCase
import com.hashsoft.audiotape.data.StorageVolumeRepository
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
    fun provideStorageAddressUseCase(
        storageVolumeRepository: StorageVolumeRepository,
        @ApplicationContext context: Context
    ): StorageAddressUseCase {
        return StorageAddressUseCase(storageVolumeRepository, context)
    }

    @Provides
    @Singleton
    fun providePlaybackRepository(): PlaybackRepository = PlaybackRepository()

    @Provides
    @Singleton
    fun provideResumeAudioRepository(): ResumeAudioRepository = ResumeAudioRepository()

    @Provides
    @Singleton
    fun provideAudioStoreRepository(@ApplicationContext context: Context): AudioStoreRepository {
        return AudioStoreRepository(context)
    }

    @Provides
    fun provideStorageItemListUseCase(
        storageVolumeRepository: StorageVolumeRepository,
        audioStoreRepository: AudioStoreRepository,
        @ApplicationContext context: Context
    ): StorageItemListUseCase {
        return StorageItemListUseCase(storageVolumeRepository, audioStoreRepository, context)
    }

    @Provides
    @Singleton
    fun providerStorageVolumeRepository(@ApplicationContext context: Context): StorageVolumeRepository {
        return StorageVolumeRepository(context)
    }

}
