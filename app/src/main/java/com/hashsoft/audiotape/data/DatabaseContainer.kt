package com.hashsoft.audiotape.data

import android.content.Context

class DatabaseContainer(private val context: Context) {
    val userSettingsRepository: UserSettingsRepository by lazy {
        UserSettingsRepository(
            AppDatabase.getInstance(context).userSettingsDao()
        )
    }
    val audioTapeRepository: AudioTapeRepository by lazy {
        AudioTapeRepository(
            AppDatabase.getInstance(context).audioTapeDao()
        )
    }
}