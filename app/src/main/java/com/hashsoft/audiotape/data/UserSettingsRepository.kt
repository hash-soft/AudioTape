package com.hashsoft.audiotape.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class UserSettingsRepository(private val userSettingsDao: UserSettingsDao) {
    fun findById(id: Int): Flow<UserSettingsDto?> {
        return userSettingsDao.findById(id).map {
            if (it == null) {
                UserSettingsDto(id, ThemeMode.SYSTEM, false, 1.0f, 1.0f)
            } else {
                UserSettingsDto(
                    it.uid,
                    stringToThemeMode(it.themeMode),
                    it.screenRestore == true,
                    it.rewindingSpeed ?: 1.0f,
                    it.forwardingSpeed ?: 1.0f
                )
            }
        }
    }

    fun getThemeMode(id: Int): Flow<ThemeMode?> {
        return userSettingsDao.getThemeMode(id)
            .map {
                stringToThemeMode(it)
            }
    }

    suspend fun updateThemeMode(id: Int, themeMode: ThemeMode) =
        userSettingsDao.upsertThemeMode(UidAndUserThemeMode(id, themeMode.name))

    suspend fun updateScreenRestore(id: Int, screenRestore: Boolean) =
        userSettingsDao.upsertScreenRestore(UidAndScreenRestore(id, screenRestore))

    suspend fun updateRewindingSpeed(id: Int, rewindingSpeed: Float) =
        userSettingsDao.upsertRewindingSpeed(UidAndRewindingSpeed(id, rewindingSpeed))

    private fun stringToThemeMode(string: String?): ThemeMode {
        return if (string == null) {
            ThemeMode.SYSTEM
        } else {
            return try {
                ThemeMode.valueOf(string)
            } catch (e: IllegalArgumentException) {
                Timber.tag(TAG).e(e, "Invalid theme mode: $string")
                ThemeMode.SYSTEM
            }
        }
    }

    companion object {
        private const val TAG = "UserSettingsRepository"
        const val DEFAULT_ID = 1
    }
}
