package com.ahmedadeltito.chatapp.domain.usecase

import com.ahmedadeltito.chatapp.sync.SyncManager
import javax.inject.Inject

// Initialize Sync in Application
class InitializeSyncUseCase @Inject constructor(
    private val syncManager: SyncManager
) {
    operator fun invoke() {
        syncManager.triggerImmediateSync()
        syncManager.schedulePeriodicSync()
    }
}