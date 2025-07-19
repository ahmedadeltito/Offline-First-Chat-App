package com.ahmedadeltito.chatapp.data

import com.ahmedadeltito.chatapp.data.local.AppDatabase
import com.ahmedadeltito.chatapp.data.local.MessageEntity
import com.ahmedadeltito.chatapp.data.remote.ChatApiService
import com.ahmedadeltito.chatapp.data.remote.MessageDto
import com.ahmedadeltito.chatapp.domain.ChatRepository
import com.ahmedadeltito.chatapp.domain.Message
import com.ahmedadeltito.chatapp.domain.MessageStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

class ChatRepositoryImpl(
    database: AppDatabase,
    private val chatApiService: ChatApiService,
    private val currentUserId: String
) : ChatRepository {

    private val messageDao = database.messageDao()

    override suspend fun sendMessage(message: Message) {
        val messageEntity = MessageEntity(
            id = message.id,
            senderId = message.senderId,
            text = message.text,
            timestamp = message.timestamp,
            isSentByMe = message.isSentByMe,
            status = message.status
        )
        messageDao.insertMessage(messageEntity)
    }

    override fun getMessages(): Flow<List<Message>> {
        return messageDao.getMessages().map { entities ->
            entities.map { entity ->
                Message(
                    id = entity.id,
                    senderId = entity.senderId,
                    text = entity.text,
                    timestamp = entity.timestamp,
                    isSentByMe = entity.isSentByMe,
                    status = entity.status
                )
            }
        }
    }

    override suspend fun updateMessageStatus(messageId: String, newStatus: MessageStatus) {
        val messageEntity = messageDao.getMessageById(messageId)
        if (messageEntity != null) {
            val updatedEntity = messageEntity.copy(status = newStatus)
            messageDao.updateMessage(updatedEntity)
        }
    }

    override suspend fun getPendingOutgoingMessages(): List<Message> {
        return messageDao.getOutgoingMessagesByStatus(MessageStatus.SENT_OR_PENDING).map { entity ->
            Message(
                id = entity.id,
                senderId = entity.senderId,
                text = entity.text,
                timestamp = entity.timestamp,
                isSentByMe = entity.isSentByMe,
                status = entity.status
            )
        }
    }

    override suspend fun sendMessageToRemote(message: Message): Boolean {
        return try {
            val messageDto = MessageDto(
                id = message.id,
                senderId = message.senderId,
                text = message.text,
                timestamp = message.timestamp.time
            )
            chatApiService.sendMessage(messageDto)
        } catch (e: Exception) {
            println("Repository: Failed to send message to remote: ${e.message}")
            false
        }
    }

    override suspend fun fetchAndSyncRemoteMessages() {
        try {
            val remoteMessages = chatApiService.fetchMessages()
            val messageEntities = remoteMessages.map { dto ->
                MessageEntity(
                    id = dto.id,
                    senderId = dto.senderId,
                    text = dto.text,
                    timestamp = Date(dto.timestamp),
                    isSentByMe = dto.senderId == currentUserId,
                    status = MessageStatus.SENT_TO_SERVER
                )
            }
            messageDao.insertMessages(messageEntities)
            println("Repository: Fetched and synced ${remoteMessages.size} remote messages.")
        } catch (e: Exception) {
            println("Repository: Failed to fetch remote messages: ${e.message}")
        }
    }
} 