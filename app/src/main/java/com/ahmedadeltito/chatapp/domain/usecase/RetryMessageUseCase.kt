package com.ahmedadeltito.chatapp.domain.usecase

import com.ahmedadeltito.chatapp.data.sync.SyncManager
import com.ahmedadeltito.chatapp.domain.ChatRepository
import com.ahmedadeltito.chatapp.domain.MessageStatus

/**
 * Use case for retrying failed messages.
 * Encapsulates the business logic for retrying message sending.
 */
class RetryMessageUseCase(
    private val chatRepository: ChatRepository,
    private val syncManager: SyncManager
) {

    /**
     * Retries sending a failed message.
     * @param messageId The ID of the message to retry
     * @param syncEnabled Whether to trigger immediate sync after retry
     * @return Result indicating success or failure with appropriate message
     */
    suspend fun execute(
        messageId: String,
        syncEnabled: Boolean
    ): RetryMessageResult = try {
        // Update the message status back to pending so it will be retried
        chatRepository.updateMessageStatus(
            messageId = messageId,
            newStatus = MessageStatus.SENT_OR_PENDING
        )

        // Trigger sync if enabled
        if (syncEnabled) {
            syncManager.triggerImmediateSync()
            RetryMessageResult.Success("Message queued for retry")
        } else {
            RetryMessageResult.Success("Message queued for retry (sync disabled)")
        }
    } catch (e: Exception) {
        RetryMessageResult.Error("Failed to retry message: ${e.message}")
    }
}

/**
 * Result of the retry message operation
 */
sealed class RetryMessageResult {
    data class Success(val message: String) : RetryMessageResult()
    data class Error(val message: String) : RetryMessageResult()
} 