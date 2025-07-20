package com.ahmedadeltito.chatapp.di

import android.content.Context
import androidx.work.WorkManager
import com.ahmedadeltito.chatapp.sync.SyncManager
import com.ahmedadeltito.chatapp.sync.SyncManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InfrastructureModule {

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager = WorkManager.getInstance(context)

    @Provides
    @Singleton
    fun provideSyncManager(workManager: WorkManager): SyncManager = SyncManagerImpl(workManager)
} 