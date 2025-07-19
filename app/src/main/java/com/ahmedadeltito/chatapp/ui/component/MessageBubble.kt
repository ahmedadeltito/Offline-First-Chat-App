package com.ahmedadeltito.chatapp.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.tooling.preview.Preview
import com.ahmedadeltito.chatapp.presentation.chat.MessageUiModel
import com.ahmedadeltito.chatapp.presentation.chat.MessageStatusUiModel.SENT_OR_PENDING
import com.ahmedadeltito.chatapp.presentation.chat.MessageStatusUiModel.SENT_TO_SERVER
import com.ahmedadeltito.chatapp.presentation.chat.MessageStatusUiModel.FAILED_TO_SEND
import com.ahmedadeltito.chatapp.util.AppConstants
import java.util.Date

@Composable
fun MessageBubble(message: MessageUiModel, isMe: Boolean, onRetryClick: ((String) -> Unit)? = null) {
    val bubbleColor =
        if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    val textColor =
        if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
    val alignment = if (isMe) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment
    ) {
        Card(
            shape = RoundedCornerShape(8.dp), colors = cardColors(containerColor = bubbleColor)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = message.text,
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                Text(
                    text = "${message.senderId} - ${timeFormat.format(message.timestamp)}",
                    color = textColor.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
                if (isMe) {
                    // Enhanced status indicator with educational context
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val statusText = when (message.status) {
                            SENT_OR_PENDING -> "⏳"
                            SENT_TO_SERVER -> "✓"
                            FAILED_TO_SEND -> "❌"
                        }
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (message.status) {
                                SENT_OR_PENDING -> "Local (pending sync)"
                                SENT_TO_SERVER -> "Synced to server"
                                FAILED_TO_SEND -> "Failed to sync"
                            },
                            color = textColor.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodySmall
                        )

                        // Show retry button for failed messages
                        if (message.status == FAILED_TO_SEND && onRetryClick != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                modifier = Modifier.clickable { onRetryClick(message.id) },
                                text = "Retry",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- Preview Composables ---

@Preview(name = "My Message - Sent", showBackground = true)
@Composable
private fun MessageBubbleMyMessageSentPreview() {
    val message = MessageUiModel(
        id = "1",
        text = "This is my message that was sent successfully!",
        senderId = AppConstants.CURRENT_USER_ID,
        timestamp = Date(System.currentTimeMillis() - 60000),
        isSentByMe = true,
        status = SENT_TO_SERVER
    )
    
    MessageBubble(
        message = message,
        isMe = true,
        onRetryClick = {}
    )
}

@Preview(name = "Other User Message", showBackground = true)
@Composable
private fun MessageBubbleOtherUserPreview() {
    val message = MessageUiModel(
        id = "2",
        text = "This is a message from another user in the conversation.",
        senderId = AppConstants.OTHER_USER_ID,
        timestamp = Date(System.currentTimeMillis() - 30000),
        isSentByMe = false,
        status = SENT_TO_SERVER
    )
    
    MessageBubble(
        message = message,
        isMe = false,
        onRetryClick = {}
    )
}

@Preview(name = "My Message - Pending", showBackground = true)
@Composable
private fun MessageBubbleMyMessagePendingPreview() {
    val message = MessageUiModel(
        id = "3",
        text = "This message is still pending and waiting to be sent.",
        senderId = AppConstants.CURRENT_USER_ID,
        timestamp = Date(System.currentTimeMillis()),
        isSentByMe = true,
        status = SENT_OR_PENDING
    )
    
    MessageBubble(
        message = message,
        isMe = true,
        onRetryClick = {}
    )
}

@Preview(name = "My Message - Failed", showBackground = true)
@Composable
private fun MessageBubbleMyMessageFailedPreview() {
    val message = MessageUiModel(
        id = "4",
        text = "This message failed to send and can be retried.",
        senderId = AppConstants.CURRENT_USER_ID,
        timestamp = Date(System.currentTimeMillis() - 5000),
        isSentByMe = true,
        status = FAILED_TO_SEND
    )
    
    MessageBubble(
        message = message,
        isMe = true,
        onRetryClick = {}
    )
}

@Preview(name = "Long Message", showBackground = true)
@Composable
private fun MessageBubbleLongMessagePreview() {
    val message = MessageUiModel(
        id = "5",
        text = "This is a very long message that demonstrates how the message bubble handles text that wraps to multiple lines. It should look good and maintain proper spacing and alignment.",
        senderId = AppConstants.CURRENT_USER_ID,
        timestamp = Date(System.currentTimeMillis() - 120000),
        isSentByMe = true,
        status = SENT_TO_SERVER
    )
    
    MessageBubble(
        message = message,
        isMe = true,
        onRetryClick = {}
    )
}