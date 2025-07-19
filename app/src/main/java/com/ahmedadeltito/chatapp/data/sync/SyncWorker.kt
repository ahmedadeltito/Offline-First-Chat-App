package com.ahmedadeltito.chatapp.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ahmedadeltito.chatapp.domain.ChatRepository
import com.ahmedadeltito.chatapp.domain.MessageStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Worker responsible for performing data synchronization
class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val chatRepository: ChatRepository // Injected dependency
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        println("SyncWorker: Starting sync operation...")
        try {
            // --- Outgoing Sync (Push local changes to remote) ---
            val pendingMessages = chatRepository.getPendingOutgoingMessages()
            if (pendingMessages.isNotEmpty()) {
                println("SyncWorker: Found ${pendingMessages.size} pending outgoing messages.")
                for (message in pendingMessages) {
                    // Check if the work has been cancelled
                    if (isStopped) {
                        println("SyncWorker: Work cancelled, stopping sync operation.")
                        return@withContext Result.success() // Return success since we're stopping intentionally
                    }
                    
                    try {
                        // Use the repository method instead of directly accessing the API
                        val success = chatRepository.sendMessageToRemote(message)
                        if (success) {
                            chatRepository.updateMessageStatus(
                                messageId = message.id,
                                newStatus = MessageStatus.SENT_TO_SERVER
                            )
                            println("SyncWorker: Successfully sent message ID: ${message.id}")
                        } else {
                            // Mark as failed for now, retry logic handled by WorkManager if transient
                            chatRepository.updateMessageStatus(
                                messageId = message.id,
                                newStatus = MessageStatus.FAILED_TO_SEND
                            )
                            println("SyncWorker: Failed to send message ID: ${message.id}")
                        }
                    } catch (e: Exception) {
                        println("SyncWorker: Exception sending message ID ${message.id}: ${e.message}")
                        // Mark as failed, WorkManager will retry this worker if it's a transient error
                        chatRepository.updateMessageStatus(
                            messageId = message.id,
                            newStatus = MessageStatus.FAILED_TO_SEND
                        )
                    }
                }
            } else {
                println("SyncWorker: No pending outgoing messages.")
            }

            // Check again before incoming sync
            if (isStopped) {
                println("SyncWorker: Work cancelled before incoming sync.")
                return@withContext Result.success()
            }

            // --- Incoming Sync (Pull remote changes to local) ---
            chatRepository.fetchAndSyncRemoteMessages()
            println("SyncWorker: Incoming sync completed.")

            Result.success() // Sync successful
        } catch (e: Exception) {
            println("SyncWorker: Sync operation failed: ${e.message}")
            Result.retry() // Retry if failed due to transient issues (e.g., network)
        }
    }
} 