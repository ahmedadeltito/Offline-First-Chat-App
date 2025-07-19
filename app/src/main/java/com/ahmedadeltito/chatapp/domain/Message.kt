package com.ahmedadeltito.chatapp.domain

import java.util.Date
import java.util.UUID

// Represents a single chat message in the domain
data class Message(
    val id: String = UUID.randomUUID().toString(), // Unique ID for the message
    val senderId: String,          // ID of the user who sent the message
    val text: String,              // The content of the message
    val timestamp: Date = Date(),  // When the message was created/sent
    val isSentByMe: Boolean,       // True if the current user sent this message
    val status: MessageStatus = MessageStatus.SENT_OR_PENDING // Current sync status
)

enum class MessageStatus {
    SENT_OR_PENDING, // Message created locally, waiting to be sent to server
    SENT_TO_SERVER,  // Message successfully sent to server
    FAILED_TO_SEND   // Message failed to send to server
} 