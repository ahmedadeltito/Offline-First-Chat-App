package com.ahmedadeltito.chatapp.data

import com.ahmedadeltito.chatapp.data.local.AppDatabase
import com.ahmedadeltito.chatapp.data.local.toDomainModel
import com.ahmedadeltito.chatapp.data.local.toEntityModel
import com.ahmedadeltito.chatapp.data.remote.ChatApiService
import com.ahmedadeltito.chatapp.data.remote.toDomainModel
import com.ahmedadeltito.chatapp.data.remote.toDtoModel
import com.ahmedadeltito.chatapp.data.remote.toEntityModel
import com.ahmedadeltito.chatapp.domain.ChatRepository
import com.ahmedadeltito.chatapp.domain.Message
import com.ahmedadeltito.chatapp.domain.MessageStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepositoryImpl(
    database: AppDatabase,
    private val chatApiService: ChatApiService,
    private val currentUserId: String
) : ChatRepository {

    private val messageDao = database.messageDao()

    override suspend fun sendMessage(message: Message) {
        messageDao.insertMessage(message.toEntityModel())
    }

    override fun getMessages(): Flow<List<Message>> = messageDao.getMessages().map { entities ->
        entities.map { entity -> entity.toDomainModel() }
    }

    override suspend fun updateMessageStatus(messageId: String, newStatus: MessageStatus) {
        val messageEntity = messageDao.getMessageById(messageId)
        if (messageEntity != null) {
            val updatedEntity = messageEntity.copy(status = newStatus)
            messageDao.updateMessage(updatedEntity)
        }
    }

    override suspend fun getPendingOutgoingMessages(): List<Message> =
        messageDao.getOutgoingMessagesByStatus(MessageStatus.SENT_OR_PENDING).map { entity ->
            entity.toDomainModel()
        }

    override suspend fun sendMessageToRemote(message: Message): Boolean = try {
        chatApiService.sendMessage(message.toDtoModel())
    } catch (e: Exception) {
        println("Repository: Failed to send message to remote: ${e.message}")
        false
    }

    override suspend fun fetchAndSyncRemoteMessages() {
        try {
            val remoteMessages = chatApiService.fetchMessage()
            val messageEntity = remoteMessages.toEntityModel(currentUserId = currentUserId)
            messageDao.insertMessage(messageEntity)
            println("Repository: Fetched and synced $remoteMessages remote messages.")
        } catch (e: Exception) {
            println("Repository: Failed to fetch remote messages: ${e.message}")
        }
    }
    
    override suspend fun getAllMessages(): List<Message> =
        messageDao.getAllMessages().map { entity -> entity.toDomainModel() }
    
    override suspend fun fetchRemoteMessages(): List<Message> = try {
            val remoteMessageDto = chatApiService.fetchMessage()
            listOf(remoteMessageDto.toDomainModel(currentUserId = currentUserId))
        } catch (e: Exception) {
            println("Repository: Failed to fetch remote messages for conflict resolution: ${e.message}")
            emptyList()
        }
    
    override suspend fun updateMessagesWithResolvedData(resolvedMessages: List<Message>) {
        try {
            // Clear existing messages and insert resolved ones
            messageDao.deleteAllMessages()
            
            // Insert resolved messages
            resolvedMessages.forEach { message ->
                messageDao.insertMessage(message.toEntityModel())
            }
            
            println("Repository: Updated local database with ${resolvedMessages.size} resolved messages")
        } catch (e: Exception) {
            println("Repository: Failed to update messages with resolved data: ${e.message}")
        }
    }
} 