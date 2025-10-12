package com.hashsoft.audiotape.ui.di.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.hashsoft.audiotape.data.FolderStateRepository
import com.hashsoft.audiotape.data.LibraryStateRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.RouteStateRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton


private val Context.routeStateStore: DataStore<Preferences> by preferencesDataStore(
    name = RouteStateRepository.DATA_STORE_NAME
)

private val Context.libraryStateStore: DataStore<Preferences> by preferencesDataStore(
    name = LibraryStateRepository.DATA_STORE_NAME
)

private val Context.libraryFolderStore: DataStore<Preferences> by preferencesDataStore(
    name = FolderStateRepository.DATA_STORE_NAME
)

private val Context.playingStateStore: DataStore<Preferences> by preferencesDataStore(
    name = PlayingStateRepository.DATA_STORE_NAME
)

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideRouteStateRepository(@ApplicationContext context: Context): RouteStateRepository {
        return RouteStateRepository(context.routeStateStore)
    }

    @Provides
    @Singleton
    fun provideLibraryStateRepository(@ApplicationContext context: Context): LibraryStateRepository {
        return LibraryStateRepository(context.libraryStateStore)
    }

    @Provides
    @Singleton
    fun provideFolderStateRepository(@ApplicationContext context: Context): FolderStateRepository {
        return FolderStateRepository(context.libraryFolderStore)
    }

    @Provides
    @Singleton
    fun providePlayingStateRepository(@ApplicationContext context: Context): PlayingStateRepository {
        return PlayingStateRepository(context.playingStateStore)
    }
}
