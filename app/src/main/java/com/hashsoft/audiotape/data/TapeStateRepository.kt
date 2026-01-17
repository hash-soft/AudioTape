package com.hashsoft.audiotape.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TapeStateRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        const val DATA_STORE_NAME = "tape_state"
        private val TAPE_LIST_SORT_ORDER = intPreferencesKey("tape_list_sort_order")
    }

    fun tapeListSortOrderFlow(): Flow<AudioTapeListSortOrder> =
        dataStore.data.map { preferences ->
            AudioTapeListSortOrder.fromInt(preferences[TAPE_LIST_SORT_ORDER] ?: 0)
        }


    suspend fun saveTapeListSortOrder(sortOrder: AudioTapeListSortOrder) {
        dataStore.edit { preferences ->
            preferences[TAPE_LIST_SORT_ORDER] = sortOrder.ordinal
        }
    }
}
