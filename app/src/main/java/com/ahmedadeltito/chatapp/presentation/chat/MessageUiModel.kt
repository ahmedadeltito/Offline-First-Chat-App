package com.ahmedadeltito.chatapp.presentation.chat

import androidx.compose.runtime.Stable
import com.ahmedadeltito.chatapp.domain.Message
import com.ahmedadeltito.chatapp.domain.MessageStatus
import java.util.Date

@Stable
data class MessageUiModel(
    val id: String,
    val text: String,
    val senderId: String,
    val timestamp: Date,
    val isSentByMe: Boolean,
    val status: MessageStatusUiModel,
)

@Stable
enum class MessageStatusUiModel {
    SENT_OR_PENDING,
    SENT_TO_SERVER,
    FAILED_TO_SEND
}

fun Message.toUiModel(): MessageUiModel = MessageUiModel(
    id = id,
    text = text,
    senderId = senderId,
    timestamp = timestamp,
    isSentByMe = isSentByMe,
    status = status.toUiModel()
)

fun MessageStatus.toUiModel(): MessageStatusUiModel = when (this) {
    MessageStatus.SENT_OR_PENDING -> MessageStatusUiModel.SENT_OR_PENDING
    MessageStatus.SENT_TO_SERVER -> MessageStatusUiModel.SENT_TO_SERVER
    MessageStatus.FAILED_TO_SEND -> MessageStatusUiModel.FAILED_TO_SEND
}