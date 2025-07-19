package com.ahmedadeltito.chatapp.data.sync

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.ahmedadeltito.chatapp.di.SyncWorkerEntryPoint
import dagger.hilt.android.EntryPointAccessors

/**
 * Hilt-aware WorkerFactory that can inject dependencies into workers.
 */
class SyncWorkerFactory : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? = when (workerClassName) {
        SyncWorker::class.java.name -> {
            val entryPoint = EntryPointAccessors.fromApplication(
                appContext,
                SyncWorkerEntryPoint::class.java
            )
            SyncWorker(
                appContext = appContext,
                workerParams = workerParameters,
                chatRepository = entryPoint.chatRepository()
            )
        }
        else -> null // Let the default factory handle other workers
    }
}