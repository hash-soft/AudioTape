package com.hashsoft.audiotape.ui.di.module

import android.content.Context
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.DatabaseContainer
import com.hashsoft.audiotape.data.UserSettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabaseContainer(@ApplicationContext context: Context): DatabaseContainer {
        return DatabaseContainer(context)
    }

    @Provides
    fun provideAudioTapeRepository(container: DatabaseContainer): AudioTapeRepository {
        return container.audioTapeRepository
    }

    @Provides
    fun provideUserSettingsRepository(container: DatabaseContainer): UserSettingsRepository {
        return container.userSettingsRepository
    }
}
