package com.ahmedadeltito.chatapp.data.sync

import androidx.lifecycle.asFlow
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.ahmedadeltito.chatapp.domain.Message
import com.ahmedadeltito.chatapp.domain.MessageStatus
import com.ahmedadeltito.chatapp.util.AppConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.concurrent.TimeUnit

// Implementation of SyncManager that handles WorkManager operations.
class SyncManagerImpl(private val workManager: WorkManager) : SyncManager {
    override fun triggerImmediateSync() {
        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>().setConstraints(constraints)
            .addTag(SyncManager.IMMEDIATE_SYNC_WORK_NAME) // Add tag so it can be cancelled
            .build()

        workManager.enqueue(syncRequest)
        println("SyncManager: Immediate sync triggered.")
    }

    override fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Only sync when connected
            .setRequiresBatteryNotLow(true) // Don't sync if battery is low
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            repeatInterval = AppConstants.SYNC_INTERVAL_MINUTES, // Sync every 15 minutes
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        ).setConstraints(constraints).build()

        workManager.enqueueUniquePeriodicWork(
            SyncManager.SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // If work already exists, keep the existing one
            syncRequest
        )
        println("SyncManager: Periodic sync scheduled.")
    }

    // Cancel any immediate sync operations that are running or queued
    override fun cancelOngoingSync() {
        workManager.cancelAllWorkByTag(SyncManager.IMMEDIATE_SYNC_WORK_NAME)
        println("SyncManager: Ongoing sync operations cancelled.")
    }

    override fun cancelPeriodicSync() {
        workManager.cancelUniqueWork(SyncManager.SYNC_WORK_NAME)
        println("SyncManager: Periodic sync cancelled.")
    }

    override fun observeSyncStatus(): Flow<String> = workManager
        .getWorkInfosForUniqueWorkLiveData(SyncManager.SYNC_WORK_NAME)
        .asFlow()
        .map { workInfos ->
            val syncInfo = workInfos.firstOrNull()
            when (syncInfo?.state) {
                WorkInfo.State.ENQUEUED -> "Queued for sync"
                WorkInfo.State.RUNNING -> AppConstants.SYNCING_STATUS
                WorkInfo.State.SUCCEEDED -> "Last synced: ${Date()}"
                WorkInfo.State.FAILED -> AppConstants.SYNC_FAILED_STATUS
                WorkInfo.State.BLOCKED -> "Sync blocked"
                WorkInfo.State.CANCELLED -> "Sync cancelled"
                else -> "No sync scheduled"
            }
        }
    
    override suspend fun resolveMessageConflicts(
        localMessages: List<Message>,
        remoteMessages: List<Message>
    ): List<Message> {
        println("SyncManager: Resolving conflicts between ${localMessages.size} local and ${remoteMessages.size} remote messages")
        
        val resolvedMessages = mutableListOf<Message>()
        val processedIds = mutableSetOf<String>()
        
        // Create maps for efficient lookup
        val localMessageMap = localMessages.associateBy { it.id }
        val remoteMessageMap = remoteMessages.associateBy { it.id }
        
        // Process all unique message IDs
        val allMessageIds = (localMessages.map { it.id } + remoteMessages.map { it.id }).distinct()
        
        for (messageId in allMessageIds) {
            val localMessage = localMessageMap[messageId]
            val remoteMessage = remoteMessageMap[messageId]
            
            when {
                // Case 1: Message exists only locally
                localMessage != null && remoteMessage == null -> {
                    resolvedMessages.add(localMessage)
                    processedIds.add(messageId)
                    println("SyncManager: Added local-only message: ${localMessage.text}")
                }
                
                // Case 2: Message exists only remotely
                localMessage == null && remoteMessage != null -> {
                    resolvedMessages.add(remoteMessage)
                    processedIds.add(messageId)
                    println("SyncManager: Added remote-only message: ${remoteMessage.text}")
                }
                
                // Case 3: Message exists in both places - resolve conflict
                localMessage != null && remoteMessage != null -> {
                    val resolvedMessage = resolveSingleMessageConflict(localMessage, remoteMessage)
                    resolvedMessages.add(resolvedMessage)
                    processedIds.add(messageId)
                    println("SyncManager: Resolved conflict for message: ${resolvedMessage.text}")
                }
            }
        }
        
        // Sort by timestamp to maintain chronological order
        val sortedMessages = resolvedMessages.sortedBy { it.timestamp }
        
        println("SyncManager: Conflict resolution complete. Final message count: ${sortedMessages.size}")
        return sortedMessages
    }
    
    /**
     * Resolves conflict between local and remote versions of the same message.
     * Uses various strategies based on the conflict type.
     */
    private fun resolveSingleMessageConflict(localMessage: Message, remoteMessage: Message): Message {
        return when {
            // Strategy 1: Content is identical - use the one with better status
            localMessage.text == remoteMessage.text -> {
                when {
                    remoteMessage.status == MessageStatus.SENT_TO_SERVER -> remoteMessage
                    localMessage.status == MessageStatus.SENT_TO_SERVER -> localMessage
                    else -> if (remoteMessage.timestamp > localMessage.timestamp) remoteMessage else localMessage
                }
            }
            
            // Strategy 2: Different content - use the more recent one
            localMessage.timestamp != remoteMessage.timestamp -> {
                if (remoteMessage.timestamp > localMessage.timestamp) {
                    println("SyncManager: Using remote version (newer timestamp) for message: ${remoteMessage.text}")
                    remoteMessage
                } else {
                    println("SyncManager: Using local version (newer timestamp) for message: ${localMessage.text}")
                    localMessage
                }
            }
            
            // Strategy 3: Same timestamp, different content - prefer remote (server authority)
            else -> {
                println("SyncManager: Using remote version (server authority) for message: ${remoteMessage.text}")
                remoteMessage
            }
        }
    }
} 