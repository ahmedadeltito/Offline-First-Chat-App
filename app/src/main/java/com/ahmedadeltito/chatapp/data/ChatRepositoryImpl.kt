package com.ahmedadeltito.chatapp.data

import com.ahmedadeltito.chatapp.data.local.AppDatabase
import com.ahmedadeltito.chatapp.data.local.toDomainModel
import com.ahmedadeltito.chatapp.data.local.toEntityModel
import com.ahmedadeltito.chatapp.data.remote.ChatApiService
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
            val remoteMessages = chatApiService.fetchMessages()
            val messageEntities = remoteMessages.map { messageDto ->
                messageDto.toEntityModel(currentUserId = currentUserId)
            }
            messageDao.insertMessages(messageEntities)
            println("Repository: Fetched and synced ${remoteMessages.size} remote messages.")
        } catch (e: Exception) {
            println("Repository: Failed to fetch remote messages: ${e.message}")
        }
    }
} 