package com.hashsoft.audiotape.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

// 最後に再生していたオーディオのあるフォルダ
// このフォルダからヒットしたAudioTapeのオーディオが最後の再生オーディオになる
class PlayingStateRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        const val DATA_STORE_NAME = "playing_state"
        private val FOLDER_PATH = stringPreferencesKey("folder_path")
    }

    private val _ramPlayingState = MutableSharedFlow<PlayingStateDto>()

    suspend fun getPlayingState(): PlayingStateDto {
        return dataStore.data.map { preferences ->
            mapPlayingStatePreferences(preferences)
        }.first()
    }

    fun playingStateFlow() = merge(_ramPlayingState, dataStore.data.map { preferences ->
        mapPlayingStatePreferences(preferences)
    }).distinctUntilChanged()

    private fun mapPlayingStatePreferences(preferences: Preferences): PlayingStateDto {
        val path = preferences[FOLDER_PATH] ?: ""
        return PlayingStateDto(
            folderPath = path
        )
    }

    suspend fun saveFolderPath(path: String) =
        dataStore.edit { preferences -> preferences[FOLDER_PATH] = path }


    fun memoryFolderPath(path: String) {
        _ramPlayingState.tryEmit(PlayingStateDto(path))
    }


}
