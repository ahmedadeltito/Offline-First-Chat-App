package com.ahmedadeltito.chatapp.di

import com.ahmedadeltito.chatapp.data.sync.SyncManager
import com.ahmedadeltito.chatapp.domain.ChatRepository
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
    ): SendMessageUseCase {
        return SendMessageUseCase(
            chatRepository = chatRepository,
            syncManager = syncManager
        )
    }

    @Provides
    @Singleton
    fun provideRetryMessageUseCase(
        chatRepository: ChatRepository,
        syncManager: SyncManager
    ): RetryMessageUseCase {
        return RetryMessageUseCase(
            chatRepository = chatRepository,
            syncManager = syncManager
        )
    }

    @Provides
    @Singleton
    fun provideToggleSyncUseCase(
        syncManager: SyncManager
    ): ToggleSyncUseCase {
        return ToggleSyncUseCase(
            syncManager = syncManager
        )
    }
    
    @Provides
    @Singleton
    fun provideSyncStatusUseCase(
        syncManager: SyncManager
    ): SyncStatusUseCase {
        return SyncStatusUseCase(
            syncManager = syncManager
        )
    }
} 