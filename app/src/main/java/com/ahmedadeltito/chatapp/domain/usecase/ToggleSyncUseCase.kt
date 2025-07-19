package com.ahmedadeltito.chatapp.domain.usecase

import com.ahmedadeltito.chatapp.data.sync.SyncManager

// Use case for toggling sync functionality.
class ToggleSyncUseCase(
    private val syncManager: SyncManager
) {
    operator fun invoke(
        currentSyncEnabled: Boolean
    ): ToggleSyncResult = try {
        val newSyncEnabled = !currentSyncEnabled

        if (newSyncEnabled) {
            // Turning sync on - trigger immediate sync to process pending messages
            syncManager.triggerImmediateSync()
            ToggleSyncResult.Success(
                newSyncEnabled = true,
                message = "Sync enabled - processing pending messages"
            )
        } else {
            // Turning sync off - cancel any ongoing sync operations
            syncManager.cancelOngoingSync()
            ToggleSyncResult.Success(
                newSyncEnabled = false,
                message = "Sync disabled - ongoing sync cancelled"
            )
        }
    } catch (e: Exception) {
        ToggleSyncResult.Error("Failed to toggle sync: ${e.message}")
    }
}

/**
 * Result of the toggle sync operation
 */
sealed class ToggleSyncResult {
    data class Success(
        val newSyncEnabled: Boolean,
        val message: String
    ) : ToggleSyncResult()

    data class Error(val message: String) : ToggleSyncResult()
} 