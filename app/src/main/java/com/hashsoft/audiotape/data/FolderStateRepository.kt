package com.hashsoft.audiotape.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hashsoft.audiotape.logic.StorageHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FolderStateRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        const val DATA_STORE_NAME = "folder_state"
        private val SELECTED_PATH = stringPreferencesKey("selected_path")
    }

    fun folderStateFlow(): Flow<FolderStateDto> {
        return dataStore.data.map { preferences ->
            mapFolderStatePreferences(preferences)
        }
    }

    private fun mapFolderStatePreferences(preferences: Preferences): FolderStateDto {
        val path = preferences[SELECTED_PATH] ?: StorageHelper.getHomePath()
        return FolderStateDto(
            selectedPath = path
        )
    }

    suspend fun saveSelectedPath(path: String) {
        dataStore.edit { preferences ->
            preferences[SELECTED_PATH] = path
        }
    }
}
