package com.hashsoft.audiotape.data

import com.hashsoft.audiotape.logic.SystemTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class UserSettingsRepository(private val userSettingsDao: UserSettingsDao) {
    fun findById(id: Int): Flow<UserSettingsDto?> {
        return userSettingsDao.findById(id).map {
            if (it == null) {
                null
            } else {
                UserSettingsDto(
                    uid = it.uid,
                    themeMode = stringToThemeMode(it.themeMode),
                    defaultSortOrder = AudioTapeSortOrder.fromInt(it.defaultSortOrder),
                    defaultRepeat = it.defaultRepeat > 0,
                    defaultVolume = it.defaultVolume,
                    defaultSpeed = it.defaultSpeed,
                    defaultPitch = it.defaultPitch,
                    screenRestore = it.screenRestore,
                    rewindingSpeed = it.rewindingSpeed,
                    forwardingSpeed = it.forwardingSpeed
                )
            }
        }
    }

    fun getThemeMode(id: Int): Flow<ThemeMode> {
        return userSettingsDao.getThemeMode(id)
            .map {
                stringToThemeMode(it)
            }
    }

    suspend fun insertAll(userSettings: UserSettingsDto): Long {
        val time = SystemTime.currentMillis()
        return userSettingsDao.insertAll(
            UserSettingsEntity(
                uid = userSettings.uid,
                themeMode = userSettings.themeMode.name,
                defaultSortOrder = userSettings.defaultSortOrder.ordinal,
                defaultRepeat = if (userSettings.defaultRepeat) 2 else 0,
                defaultVolume = userSettings.defaultVolume,
                defaultSpeed = userSettings.defaultSpeed,
                defaultPitch = userSettings.defaultPitch,
                screenRestore = userSettings.screenRestore,
                rewindingSpeed = userSettings.rewindingSpeed,
                forwardingSpeed = userSettings.forwardingSpeed,
                createTime = time,
                updateTime = time,
            )
        )

    }

    suspend fun updateThemeMode(id: Int, themeMode: ThemeMode) =
        userSettingsDao.updateThemeMode(UserSettingsEntityThemeMode(id, themeMode.name))

    private fun stringToThemeMode(string: String?): ThemeMode {
        return if (string == null) {
            ThemeMode.SYSTEM
        } else {
            return try {
                ThemeMode.valueOf(string)
            } catch (e: IllegalArgumentException) {
                Timber.tag(TAG).w(e, "Invalid theme mode: $string")
                ThemeMode.SYSTEM
            }
        }
    }

    suspend fun updateDefaultSortOrder(id: Int, sortOrder: AudioTapeSortOrder) =
        userSettingsDao.updateDefaultSortOrder(
            UserSettingsEntityDefaultSortOrder(
                id,
                sortOrder.ordinal
            )
        )

    suspend fun updateDefaultRepeat(id: Int, repeat: Boolean) =
        userSettingsDao.updateDefaultRepeat(
            UserSettingsEntityDefaultRepeat(
                id,
                if (repeat) 2 else 0
            )
        )

    suspend fun updateDefaultVolume(id: Int, volume: Float) =
        userSettingsDao.updateDefaultVolume(UserSettingsEntityDefaultVolume(id, volume))

    suspend fun updateDefaultSpeed(id: Int, speed: Float) =
        userSettingsDao.updateDefaultSpeed(UserSettingsEntityDefaultSpeed(id, speed))

    suspend fun updateDefaultPitch(id: Int, pitch: Float) =
        userSettingsDao.updateDefaultPitch(UserSettingsEntityDefaultPitch(id, pitch))


    companion object {
        private const val TAG = "UserSettingsRepository"
        const val DEFAULT_ID = 1
    }
}
