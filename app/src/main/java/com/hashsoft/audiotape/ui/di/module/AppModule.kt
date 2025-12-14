package com.hashsoft.audiotape.ui.di.module

import android.content.Context
import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.ControllerRepository
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
        @ApplicationContext context: Context
    ): StorageAddressUseCase = StorageAddressUseCase(context)


    @Provides
    @Singleton
    fun provideAudioStoreRepository(@ApplicationContext context: Context): AudioStoreRepository =
        AudioStoreRepository(context)

    @Provides
    fun provideStorageItemListUseCase(
        audioStoreRepository: AudioStoreRepository,
    ): StorageItemListUseCase = StorageItemListUseCase(audioStoreRepository)


    @Provides
    @Singleton
    fun providerStorageVolumeRepository(@ApplicationContext context: Context): StorageVolumeRepository =
        StorageVolumeRepository(context)

    @Provides
    @Singleton
    fun providerControllerRepository(): ControllerRepository = ControllerRepository()

}
