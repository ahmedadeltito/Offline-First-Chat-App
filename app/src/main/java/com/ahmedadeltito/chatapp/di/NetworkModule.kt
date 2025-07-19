package com.ahmedadeltito.chatapp.di

import com.ahmedadeltito.chatapp.data.remote.ChatApiService
import com.ahmedadeltito.chatapp.data.remote.ChatApiServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideChatApiService(): ChatApiService {
        return ChatApiServiceImpl()
    }
} 