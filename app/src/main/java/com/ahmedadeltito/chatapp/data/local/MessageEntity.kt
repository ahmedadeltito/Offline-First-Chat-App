package com.ahmedadeltito.chatapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ahmedadeltito.chatapp.domain.Message
import com.ahmedadeltito.chatapp.domain.MessageStatus
import java.util.Date

// Room Entity representing a message in the local database
@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val senderId: String,
    val text: String,
    val timestamp: Date,
    val isSentByMe: Boolean,
    val status: MessageStatus // Sync status for this message
)

// Helper function to convert Entity to Domain Model
fun MessageEntity.toDomainModel(): Message = Message(
    id = id,
    senderId = senderId,
    text = text,
    timestamp = timestamp,
    isSentByMe = isSentByMe,
    status = status
)

// Helper function to convert Domain Model to Entity
fun Message.toEntity(): MessageEntity {
    return MessageEntity(
        id = id,
        senderId = senderId,
        text = text,
        timestamp = timestamp,
        isSentByMe = isSentByMe,
        status = status
    )
} 