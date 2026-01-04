/**
 * データベース（Room）に関連する依存関係の提供を定義するファイル。
 */
package com.hashsoft.audiotape.ui.di.module

import android.content.Context
import com.hashsoft.audiotape.data.AudioTapeRepository
import com.hashsoft.audiotape.data.DatabaseContainer
import com.hashsoft.audiotape.data.UserSettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton


/**
 * データベースに関連する依存関係を提供するHiltモジュール
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * [DatabaseContainer] を提供する
     *
     * @param context アプリケーションコンテキスト
     * @return データベースとリポジトリを保持するコンテナ
     */
    @Provides
    @Singleton
    fun provideDatabaseContainer(@ApplicationContext context: Context): DatabaseContainer {
        return DatabaseContainer(context)
    }

    /**
     * [AudioTapeRepository] を提供する
     *
     * @param container データベースコンテナ
     * @return オーディオテープのデータを管理するリポジトリ
     */
    @Provides
    fun provideAudioTapeRepository(container: DatabaseContainer): AudioTapeRepository {
        return container.audioTapeRepository
    }

    /**
     * [UserSettingsRepository] を提供する
     *
     * @param container データベースコンテナ
     * @return ユーザー設定を管理するリポジトリ
     */
    @Provides
    fun provideUserSettingsRepository(container: DatabaseContainer): UserSettingsRepository {
        return container.userSettingsRepository
    }
}
