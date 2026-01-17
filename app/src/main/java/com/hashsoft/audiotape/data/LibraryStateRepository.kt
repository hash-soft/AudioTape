package com.hashsoft.audiotape.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LibraryStateRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        const val DATA_STORE_NAME = "library_state"
        private const val FOLDER_NAME = "Folder"
        private const val TAPE_NAME = "Tape"
        const val FOLDER_NAME_INDEX = 0
        const val TAPE_NAME_INDEX = 1
        private val TABS = listOf(
            LibraryTab(FOLDER_NAME, Icons.Default.Folder),
            LibraryTab(TAPE_NAME, Icons.Default.LibraryMusic)
        )
        private val SELECTED_TAB_NAME = stringPreferencesKey("selected_tab_name")
    }

    fun libraryStateFlow(): Flow<LibraryStateDto> =
        dataStore.data.map { preferences ->
            mapLibraryStatePreferences(preferences)
        }

    private fun mapLibraryStatePreferences(preferences: Preferences): LibraryStateDto {
        val tabName = preferences[SELECTED_TAB_NAME] ?: TABS[FOLDER_NAME_INDEX].name
        val index = TABS.indexOfFirst { it.name == tabName }
        return LibraryStateDto(
            selectedTabIndex = if (index < 0 || index >= TABS.size) FOLDER_NAME_INDEX else index
        )
    }

    fun tabs() = TABS

    suspend fun saveSelectedTabName(tabIndex: Int) {
        val tabName = TABS.getOrNull(tabIndex)?.name ?: FOLDER_NAME
        dataStore.edit { preferences ->
            preferences[SELECTED_TAB_NAME] = tabName
        }
    }
}
