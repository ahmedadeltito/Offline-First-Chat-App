package com.ahmedadeltito.chatapp.domain.usecase

import com.ahmedadeltito.chatapp.sync.SyncManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Use case for observing sync status.
class SyncStatusUseCase @Inject constructor(
    private val syncManager: SyncManager
) {
    // Returns a Flow that emits sync status updates.
    fun observeSyncStatus(): Flow<String> = syncManager.observeSyncStatus()
} 