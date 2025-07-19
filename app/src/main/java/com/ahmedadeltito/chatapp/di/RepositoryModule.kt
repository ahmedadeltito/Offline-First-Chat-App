package com.ahmedadeltito.chatapp.di

import com.ahmedadeltito.chatapp.data.ChatRepositoryImpl
import com.ahmedadeltito.chatapp.data.local.AppDatabase
import com.ahmedadeltito.chatapp.data.remote.ChatApiService
import com.ahmedadeltito.chatapp.domain.ChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideChatRepository(
        database: AppDatabase,
        chatApiService: ChatApiService
    ): ChatRepository = ChatRepositoryImpl(
        database = database,
        chatApiService = chatApiService,
        currentUserId = "myUserId"
    )
} 