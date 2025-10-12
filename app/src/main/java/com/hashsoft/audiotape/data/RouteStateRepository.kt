package com.hashsoft.audiotape.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * ルートの状態を管理する
 *
 * @property dataStore DataStoreインスタンス
 */
class RouteStateRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        /**
         * DataStoreの名前
         */
        const val DATA_STORE_NAME = "route_state"

        /**
         * 開始画面のキー
         */
        private val START_SCREEN_NAME = stringPreferencesKey("start_screen")
    }

    /**
     * ルートの状態を取得する
     *
     * @return ルートの状態
     */
    suspend fun getRouteState(): RouteStateDto {
        return dataStore.data.map { preferences ->
            mapRouteStatePreferences(preferences)
        }.first()
    }

    /**
     * Preferencesを[RouteStateDto]にマップする
     *
     * @param preferences マッピングするPreferences
     * @return マッピングされた[RouteStateDto]
     */
    private fun mapRouteStatePreferences(preferences: Preferences): RouteStateDto {
        return RouteStateDto(
            startScreen = preferences[START_SCREEN_NAME] ?: ""
        )
    }

    /**
     * 開始画面を保存する
     *
     * @param startScreen 保存する開始画面名
     */
    suspend fun saveStartScreen(startScreen: String) {
        dataStore.edit { preferences ->
            preferences[START_SCREEN_NAME] = startScreen
        }
    }

}
