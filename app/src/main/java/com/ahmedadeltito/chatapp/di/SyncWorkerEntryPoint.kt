package com.ahmedadeltito.chatapp.di

import com.ahmedadeltito.chatapp.data.sync.SyncManager
import com.ahmedadeltito.chatapp.domain.ChatRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Entry point for SyncWorker dependencies.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface SyncWorkerEntryPoint {
    fun chatRepository(): ChatRepository
    fun syncManager(): SyncManager
}