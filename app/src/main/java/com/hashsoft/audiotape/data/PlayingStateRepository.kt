package com.hashsoft.audiotape.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// 最後に再生していたオーディオのあるフォルダ
// このフォルダからヒットしたAudioTapeのオーディオが最後の再生オーディオになる
class PlayingStateRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        const val DATA_STORE_NAME = "playing_state"
        private val FOLDER_PATH = stringPreferencesKey("folder_path")
    }

    suspend fun getPlayingState(): PlayingStateDto {
        return dataStore.data.map { preferences ->
            mapPlayingStatePreferences(preferences)
        }.first()
    }

    fun playingStateFlow() = dataStore.data.map { preferences ->
        mapPlayingStatePreferences(preferences)
    }

    private fun mapPlayingStatePreferences(preferences: Preferences): PlayingStateDto {
        val path = preferences[FOLDER_PATH] ?: ""
        return PlayingStateDto(
            folderPath = path
        )
    }

    suspend fun saveFolderPath(path: String) {
        dataStore.edit { preferences ->
            preferences[FOLDER_PATH] = path
        }
    }
}
