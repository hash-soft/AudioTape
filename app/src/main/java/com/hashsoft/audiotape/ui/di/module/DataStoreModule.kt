/**
 * データ保存（Jetpack DataStore）に関連する依存関係の提供を定義するファイル
 */
package com.hashsoft.audiotape.ui.di.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.hashsoft.audiotape.data.FolderStateRepository
import com.hashsoft.audiotape.data.LibraryStateRepository
import com.hashsoft.audiotape.data.PlayingStateRepository
import com.hashsoft.audiotape.data.RouteStateRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton


/** 画面遷移状態を保存するためのDataStore */
private val Context.routeStateStore: DataStore<Preferences> by preferencesDataStore(
    name = RouteStateRepository.DATA_STORE_NAME
)

/** ライブラリ画面の状態を保存するためのDataStore */
private val Context.libraryStateStore: DataStore<Preferences> by preferencesDataStore(
    name = LibraryStateRepository.DATA_STORE_NAME
)

/** フォルダ画面の状態を保存するためのDataStore */
private val Context.libraryFolderStore: DataStore<Preferences> by preferencesDataStore(
    name = FolderStateRepository.DATA_STORE_NAME
)

/** 再生状態を保存するためのDataStore */
private val Context.playingStateStore: DataStore<Preferences> by preferencesDataStore(
    name = PlayingStateRepository.DATA_STORE_NAME
)

/**
 * DataStoreに関連する依存関係を提供するHiltモジュール
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    /**
     * [RouteStateRepository] を提供する
     *
     * @param context アプリケーションコンテキスト
     * @return 画面遷移状態を管理するリポジトリ
     */
    @Provides
    @Singleton
    fun provideRouteStateRepository(@ApplicationContext context: Context): RouteStateRepository {
        return RouteStateRepository(context.routeStateStore)
    }

    /**
     * [LibraryStateRepository] を提供する
     *
     * @param context アプリケーションコンテキスト
     * @return ライブラリ画面の状態を管理するリポジトリ
     */
    @Provides
    @Singleton
    fun provideLibraryStateRepository(@ApplicationContext context: Context): LibraryStateRepository {
        return LibraryStateRepository(context.libraryStateStore)
    }

    /**
     * [FolderStateRepository] を提供する
     *
     * @param context アプリケーションコンテキスト
     * @return フォルダ画面の状態を管理するリポジトリ
     */
    @Provides
    @Singleton
    fun provideFolderStateRepository(@ApplicationContext context: Context): FolderStateRepository {
        return FolderStateRepository(context.libraryFolderStore)
    }

    /**
     * [PlayingStateRepository] を提供する
     *
     * @param context アプリケーションコンテキスト
     * @return 再生状態を管理するリポジトリ
     */
    @Provides
    @Singleton
    fun providePlayingStateRepository(@ApplicationContext context: Context): PlayingStateRepository {
        return PlayingStateRepository(context.playingStateStore)
    }
}
