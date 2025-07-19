package com.ahmedadeltito.chatapp.domain.usecase

import com.ahmedadeltito.chatapp.data.sync.SyncManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing sync status.
 * This encapsulates the business logic for monitoring sync operations.
 */
class SyncStatusUseCase @Inject constructor(
    private val syncManager: SyncManager
) {
    
    /**
     * Returns a Flow that emits sync status updates.
     * This abstracts away the WorkManager implementation details.
     */
    fun observeSyncStatus(): Flow<String> {
        return syncManager.observeSyncStatus()
    }
    
    /**
     * Initializes the sync system.
     * This triggers immediate sync and schedules periodic sync.
     */
    suspend fun initializeSync() {
        syncManager.triggerImmediateSync()
        syncManager.schedulePeriodicSync()
    }
} 