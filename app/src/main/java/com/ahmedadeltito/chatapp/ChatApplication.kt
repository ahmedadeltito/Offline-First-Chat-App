package com.ahmedadeltito.chatapp

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.ahmedadeltito.chatapp.sync.SyncWorkerFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChatApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Configure WorkManager with Hilt WorkerFactory for dependency injection
        val workManagerConfiguration = Configuration.Builder()
            .setWorkerFactory(SyncWorkerFactory())
            .build()
        
        // Initialize WorkManager with custom configuration
        WorkManager.initialize(this, workManagerConfiguration)
    }
} 