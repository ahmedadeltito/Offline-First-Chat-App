package com.ahmedadeltito.chatapp.data.remote

import com.ahmedadeltito.chatapp.data.local.MessageEntity
import com.ahmedadeltito.chatapp.domain.Message
import com.ahmedadeltito.chatapp.domain.MessageStatus
import java.util.Date

// Data Transfer Object (DTO) for messages sent to/received from API
data class MessageDto(
    val id: String,
    val senderId: String,
    val text: String,
    val timestamp: Long // Use Long for timestamp in DTOs
)

fun MessageDto.toEntityModel(currentUserId: String): MessageEntity = MessageEntity(
    id = id,
    senderId = senderId,
    text = text,
    timestamp = Date(timestamp),
    isSentByMe = senderId == currentUserId,
    status = MessageStatus.SENT_TO_SERVER
)

fun Message.toDtoModel(): MessageDto = MessageDto(
    id = id,
    senderId = senderId,
    text = text,
    timestamp = timestamp.time
)