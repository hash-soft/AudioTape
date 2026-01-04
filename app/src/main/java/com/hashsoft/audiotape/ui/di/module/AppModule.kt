/**
 * アプリケーション全体で使用される共通の依存関係の提供を定義するファイル
 */
package com.hashsoft.audiotape.ui.di.module

import android.content.Context
import com.hashsoft.audiotape.data.AudioStoreRepository
import com.hashsoft.audiotape.data.ControllerRepository
import com.hashsoft.audiotape.data.StorageAddressUseCase
import com.hashsoft.audiotape.data.StorageItemListUseCase
import com.hashsoft.audiotape.data.StorageVolumeRepository
import com.hashsoft.audiotape.ui.AudioController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton


/**
 * アプリケーション全体で共通して使用される依存関係を提供するHiltモジュール
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * [AudioController] を提供する
     *
     * @return オーディオ再生を制御するコントローラー
     */
    @Provides
    @Singleton
    fun provideAudioController(): AudioController = AudioController()

    /**
     * [StorageAddressUseCase] を提供する
     *
     * @param context アプリケーションコンテキスト
     * @return ストレージのパスやアドレスに関するユースケース
     */
    @Provides
    fun provideStorageAddressUseCase(
        @ApplicationContext context: Context
    ): StorageAddressUseCase = StorageAddressUseCase(context)


    /**
     * [AudioStoreRepository] を提供する
     *
     * @param context アプリケーションコンテキスト
     * @return 端末内のオーディオファイルを管理するリポジトリ
     */
    @Provides
    @Singleton
    fun provideAudioStoreRepository(@ApplicationContext context: Context): AudioStoreRepository =
        AudioStoreRepository(context)

    /**
     * [StorageItemListUseCase] を提供する
     *
     * @param audioStoreRepository オーディオストアリポジトリ
     * @return ストレージ内のアイテム一覧を取得するユースケース
     */
    @Provides
    fun provideStorageItemListUseCase(
        audioStoreRepository: AudioStoreRepository,
    ): StorageItemListUseCase = StorageItemListUseCase(audioStoreRepository)


    /**
     * [StorageVolumeRepository] を提供する
     *
     * @param context アプリケーションコンテキスト
     * @return ストレージボリューム（内部・外部ストレージ）を管理するリポジトリ
     */
    @Provides
    @Singleton
    fun providerStorageVolumeRepository(@ApplicationContext context: Context): StorageVolumeRepository =
        StorageVolumeRepository(context)

    /**
     * [ControllerRepository] を提供する
     *
     * @return コントローラーの状態を管理するリポジトリ
     */
    @Provides
    @Singleton
    fun providerControllerRepository(): ControllerRepository = ControllerRepository()

}
