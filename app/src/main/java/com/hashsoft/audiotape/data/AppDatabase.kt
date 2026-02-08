package com.hashsoft.audiotape.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserSettingsEntity::class, AudioTapeEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun audioTapeDao(): AudioTapeDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "audio_tape_database"
                ).fallbackToDestructiveMigration(true).build().also { Instance = it }
            }
        }
    }
}
