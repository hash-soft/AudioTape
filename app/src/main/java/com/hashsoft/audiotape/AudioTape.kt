package com.hashsoft.audiotape

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.hashsoft.audiotape.data.DatabaseContainer
import com.hashsoft.audiotape.data.FolderStateRepository
import com.hashsoft.audiotape.data.LibraryStateRepository
import com.hashsoft.audiotape.data.PlaybackRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import timber.log.Timber

private val Context.libraryStateStore: DataStore<Preferences> by preferencesDataStore(
    name = LibraryStateRepository.DATA_STORE_NAME
)

private val Context.libraryFolderStore: DataStore<Preferences> by preferencesDataStore(
    name = FolderStateRepository.DATA_STORE_NAME
)

private val Context.playingStateStore: DataStore<Preferences> by preferencesDataStore(
    name = PlayingStateRepository.DATA_STORE_NAME
)

class AudioTape : Application() {
    lateinit var databaseContainer: DatabaseContainer
    lateinit var libraryStateRepository: LibraryStateRepository
    lateinit var libraryFolderRepository: FolderStateRepository
    lateinit var playingStateRepository: PlayingStateRepository
    lateinit var playbackRepository: PlaybackRepository

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(TimberLine())
        databaseContainer = DatabaseContainer(this)
        libraryStateRepository = LibraryStateRepository(libraryStateStore)
        libraryFolderRepository = FolderStateRepository(libraryFolderStore)
        playingStateRepository = PlayingStateRepository(playingStateStore)
        playbackRepository = PlaybackRepository()
    }
}