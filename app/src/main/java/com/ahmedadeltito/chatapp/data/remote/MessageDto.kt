package com.ahmedadeltito.chatapp.data.remote

import com.ahmedadeltito.chatapp.domain.Message
import java.util.Date

// Data Transfer Object (DTO) for messages sent to/received from API
data class MessageDto(
    val id: String,
    val senderId: String,
    val text: String,
    val timestamp: Long // Use Long for timestamp in DTOs
)

fun MessageDto.toDomainModel(): Message {
    return Message(
        id = id,
        senderId = senderId,
        text = text,
        timestamp = Date(timestamp),
        isSentByMe = false, // This will be set correctly by the repository/sync logic
        status = com.ahmedadeltito.chatapp.domain.MessageStatus.SENT_TO_SERVER
    )
}