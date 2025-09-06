package com.hashsoft.audiotape.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LibraryStateRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        const val DATA_STORE_NAME = "library_state"
        private val FOLDER_NAME = "Folder"
        private val TAPE_NAME = "Tape"
        private val TABS = listOf(
            LibraryTab(FOLDER_NAME, Icons.Default.Folder),
            LibraryTab(TAPE_NAME, Icons.Default.LibraryMusic)
        )
        private val SELECTED_TAB_NAME = stringPreferencesKey("selected_tab_name")
    }

    suspend fun getLibraryState(): LibraryStateDto {
        return dataStore.data.map { preferences ->
            mapLibraryStatePreferences(preferences)
        }.first()
    }

    private fun mapLibraryStatePreferences(preferences: Preferences): LibraryStateDto {
        val tabName = preferences[SELECTED_TAB_NAME] ?: TABS[0].name
        val index = TABS.indexOfFirst { it.name == tabName }
        return LibraryStateDto(
            selectedTabIndex = if (index < 0 || index >= TABS.size) 0 else index
        )
    }

    fun tabs() = TABS

    suspend fun saveSelectedTabName(tabIndex: Int) {
        val tabName =
            if (tabIndex < 0 || tabIndex >= TABS.size) TABS[0].name else TABS[tabIndex].name
        dataStore.edit { preferences ->
            preferences[SELECTED_TAB_NAME] = tabName
        }
    }
}
