package com.ahmedadeltito.chatapp.di

import com.ahmedadeltito.chatapp.data.sync.SyncManager
import com.ahmedadeltito.chatapp.domain.ChatRepository
import com.ahmedadeltito.chatapp.domain.usecase.InitializeSyncUseCase
import com.ahmedadeltito.chatapp.domain.usecase.ObserveMessagesUseCase
import com.ahmedadeltito.chatapp.domain.usecase.RefreshMessagesUseCase
import com.ahmedadeltito.chatapp.domain.usecase.RetryMessageUseCase
import com.ahmedadeltito.chatapp.domain.usecase.SendMessageUseCase
import com.ahmedadeltito.chatapp.domain.usecase.SyncStatusUseCase
import com.ahmedadeltito.chatapp.domain.usecase.ToggleSyncUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideSendMessageUseCase(
        chatRepository: ChatRepository,
        syncManager: SyncManager
    ): SendMessageUseCase = SendMessageUseCase(
        chatRepository = chatRepository,
        syncManager = syncManager
    )

    @Provides
    @Singleton
    fun provideRetryMessageUseCase(
        chatRepository: ChatRepository,
        syncManager: SyncManager
    ): RetryMessageUseCase = RetryMessageUseCase(
        chatRepository = chatRepository,
        syncManager = syncManager
    )

    @Provides
    @Singleton
    fun provideToggleSyncUseCase(
        syncManager: SyncManager
    ): ToggleSyncUseCase = ToggleSyncUseCase(syncManager = syncManager)

    @Provides
    @Singleton
    fun provideSyncStatusUseCase(
        syncManager: SyncManager
    ): SyncStatusUseCase = SyncStatusUseCase(syncManager = syncManager)

    @Provides
    @Singleton
    fun provideInitializeSyncUseCase(
        syncManager: SyncManager
    ): InitializeSyncUseCase = InitializeSyncUseCase(syncManager = syncManager)

    @Provides
    @Singleton
    fun provideObserveMessagesUseCase(
        chatRepository: ChatRepository
    ): ObserveMessagesUseCase = ObserveMessagesUseCase(chatRepository = chatRepository)

    @Provides
    @Singleton
    fun provideRefreshMessagesUseCase(
        chatRepository: ChatRepository
    ): RefreshMessagesUseCase = RefreshMessagesUseCase(chatRepository = chatRepository)
} 