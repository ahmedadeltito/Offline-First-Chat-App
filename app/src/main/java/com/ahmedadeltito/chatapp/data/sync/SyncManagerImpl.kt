package com.ahmedadeltito.chatapp.data.sync

import androidx.lifecycle.asFlow
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
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
} 