package com.ahmedadeltito.chatapp.data.sync

import kotlinx.coroutines.flow.Flow
import com.ahmedadeltito.chatapp.domain.Message

// Interface for sync management operations.
interface SyncManager {
    fun triggerImmediateSync()
    fun schedulePeriodicSync()
    fun cancelOngoingSync()
    fun cancelPeriodicSync()
    fun observeSyncStatus(): Flow<String>
    suspend fun resolveMessageConflicts(
        localMessages: List<Message>,
        remoteMessages: List<Message>
    ): List<Message>

    companion object {
        const val IMMEDIATE_SYNC_WORK_NAME = "immediate_sync_work"
        const val SYNC_WORK_NAME = "chat_sync_work"
    }
}