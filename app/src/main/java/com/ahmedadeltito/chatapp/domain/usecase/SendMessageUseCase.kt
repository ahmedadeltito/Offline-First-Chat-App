package com.ahmedadeltito.chatapp.domain.usecase

import com.ahmedadeltito.chatapp.data.sync.SyncManager
import com.ahmedadeltito.chatapp.domain.ChatRepository
import com.ahmedadeltito.chatapp.domain.Message
import com.ahmedadeltito.chatapp.domain.MessageStatus
import java.util.Date

/**
 * Use case for sending messages with sync management.
 * Encapsulates the business logic for sending messages and handling sync operations.
 */
class SendMessageUseCase(
    private val chatRepository: ChatRepository,
    private val syncManager: SyncManager
) {

    /**
     * Sends a message with optional sync triggering.
     * @param text The message text to send
     * @param senderId The ID of the sender
     * @param syncEnabled Whether to trigger immediate sync after sending
     * @return Result indicating success or failure with appropriate message
     */
    suspend fun execute(
        text: String,
        senderId: String,
        syncEnabled: Boolean
    ): SendMessageResult = try {
        // Create the message
        val message = Message(
            senderId = senderId,
            text = text,
            isSentByMe = true,
            timestamp = Date(),
            status = MessageStatus.SENT_OR_PENDING
        )

        // Save message locally
        chatRepository.sendMessage(message)

        // Trigger sync if enabled
        if (syncEnabled) {
            syncManager.triggerImmediateSync()
            SendMessageResult.Success("Message sent and queued for sync")
        } else {
            SendMessageResult.Success("Message saved locally (sync disabled)")
        }
    } catch (e: Exception) {
        SendMessageResult.Error("Failed to send message: ${e.message}")
    }
}

/**
 * Result of the send message operation
 */
sealed class SendMessageResult {
    data class Success(val message: String) : SendMessageResult()
    data class Error(val message: String) : SendMessageResult()
} 