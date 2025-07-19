package com.ahmedadeltito.chatapp.data.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.lifecycle.asFlow
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * Interface for sync management operations.
 * This allows for better testing and platform agnostic use cases.
 */
interface SyncManager {
    fun triggerImmediateSync()
    fun schedulePeriodicSync()
    fun cancelOngoingSync()
    fun cancelPeriodicSync()
    
    /**
     * Returns a Flow that emits sync status updates.
     * This abstracts WorkManager status observation.
     */
    fun observeSyncStatus(): Flow<String>

    companion object {
        const val IMMEDIATE_SYNC_WORK_NAME = "immediate_sync_work"
        const val SYNC_WORK_NAME = "chat_sync_work"
    }
}

/**
 * Implementation of SyncManager that handles WorkManager operations.
 */
class SyncManagerImpl(private val workManager: WorkManager) : SyncManager {
    override fun triggerImmediateSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
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
            repeatInterval = 15, // Sync every 15 minutes
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            SyncManager.SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // If work already exists, keep the existing one
            syncRequest
        )
        println("SyncManager: Periodic sync scheduled.")
    }

    override fun cancelOngoingSync() {
        // Cancel any immediate sync operations that are running or queued
        workManager.cancelAllWorkByTag(SyncManager.IMMEDIATE_SYNC_WORK_NAME)
        println("SyncManager: Ongoing sync operations cancelled.")
    }

    override fun cancelPeriodicSync() {
        workManager.cancelUniqueWork(SyncManager.SYNC_WORK_NAME)
        println("SyncManager: Periodic sync cancelled.")
    }
    
    override fun observeSyncStatus(): Flow<String> {
        return workManager
            .getWorkInfosForUniqueWorkLiveData(SyncManager.SYNC_WORK_NAME)
            .asFlow()
            .map { workInfos ->
                val syncInfo = workInfos.firstOrNull()
                when {
                    syncInfo?.state == WorkInfo.State.ENQUEUED -> "Queued for sync"
                    syncInfo?.state == WorkInfo.State.RUNNING -> "Syncing..."
                    syncInfo?.state == WorkInfo.State.SUCCEEDED -> "Last synced: ${Date()}"
                    syncInfo?.state == WorkInfo.State.FAILED -> "Sync failed"
                    syncInfo?.state == WorkInfo.State.BLOCKED -> "Sync blocked"
                    syncInfo?.state == WorkInfo.State.CANCELLED -> "Sync cancelled"
                    else -> "No sync scheduled"
                }
            }
    }
} 