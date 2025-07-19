package com.ahmedadeltito.chatapp.domain

import kotlinx.coroutines.flow.Flow

// Defines the contract for chat message operations
interface ChatRepository {

    /**
     * Sends a new message. This will typically save locally first, then queue for sync.
     * @param message The message to send.
     */
    suspend fun sendMessage(message: Message)

    /**
     * Gets a flow of all messages, ordered by timestamp (ascending).
     * The UI will observe this flow for real-time updates from the local database.
     * @return A Flow emitting lists of Messages.
     */
    fun getMessages(): Flow<List<Message>>

    /**
     * Fetches messages from the remote server and updates the local database.
     * This is part of the incoming sync process.
     */
    suspend fun fetchAndSyncRemoteMessages()

    /**
     * Retrieves messages that are locally created but not yet sent to the server.
     * Used by the sync worker to identify messages to push.
     * @return A list of messages pending synchronization.
     */
    suspend fun getPendingOutgoingMessages(): List<Message>

    /**
     * Updates the status of a message (e.g., after successful send).
     * @param messageId The ID of the message to update.
     * @param newStatus The new status to set.
     */
    suspend fun updateMessageStatus(messageId: String, newStatus: MessageStatus)

    /**
     * Sends a message to the remote server. Used by the sync worker.
     * @param message The message to send to the server.
     * @return true if the message was successfully sent, false otherwise.
     */
    suspend fun sendMessageToRemote(message: Message): Boolean
    
    /**
     * Gets all messages from the local database.
     * Used for conflict resolution to compare with remote messages.
     * @return A list of all local messages.
     */
    suspend fun getAllMessages(): List<Message>
    
    /**
     * Fetches messages from the remote server without updating local database.
     * Used for conflict resolution to compare with local messages.
     * @return A list of messages from the remote server.
     */
    suspend fun fetchRemoteMessages(): List<Message>
    
    /**
     * Updates the local database with resolved messages from conflict resolution.
     * This method replaces the existing messages with the resolved set.
     * @param resolvedMessages The list of messages after conflict resolution.
     */
    suspend fun updateMessagesWithResolvedData(resolvedMessages: List<Message>)
} 