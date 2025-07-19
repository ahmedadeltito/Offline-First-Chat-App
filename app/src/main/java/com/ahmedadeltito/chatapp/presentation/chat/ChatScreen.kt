package com.ahmedadeltito.chatapp.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ahmedadeltito.chatapp.domain.Message
import com.ahmedadeltito.chatapp.domain.MessageStatus
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * ChatScreen is now a pure UI component that:
 * 1. Receives UI state as a parameter
 * 2. Receives event handlers as parameters
 * 3. Has no direct dependency on ViewModel
 * 4. Is easily testable and reusable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    uiState: ChatUiState,
    onEvent: (ChatUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    // --- Collect UI State ---
    val listState = rememberLazyListState() // For scrolling to bottom

    // --- Auto-scroll to bottom when new messages arrive ---
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.lastIndex)
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Column {
                    Text("Offline-First Chat")
                    Text(
                        "WorkManager Demo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        )
    }, snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- Educational Header with Sync Toggle ---
            EducationalHeader(
                syncEnabled = uiState.syncEnabled,
                onSyncToggle = { onEvent(ChatUiEvent.SyncToggleClicked) })

            // --- Messages List ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center // For loading/empty state
            ) {
                when {
                    uiState.isLoading -> CircularProgressIndicator()
                    uiState.error != null -> Text(
                        "Error: ${uiState.error}", color = MaterialTheme.colorScheme.error
                    )

                    uiState.messages.isEmpty() -> Text("Start a conversation!")
                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.messages, key = { it.id }) { message ->
                                MessageBubble(
                                    message = message,
                                    isMe = message.senderId == uiState.currentUserId, // Use currentUserId from UI state
                                    onRetryClick = {
                                        onEvent(ChatUiEvent.RetryMessageClicked(messageId = it))
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // --- Enhanced Sync Status Indicator ---
            SyncStatusCard(syncStatus = uiState.syncStatus)

            // --- Message Input Bar ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.currentInput,
                    onValueChange = { onEvent(ChatUiEvent.InputChanged(newInput = it)) }, // UI sends event
                    label = { Text("Type a message") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    enabled = !uiState.isSending // Disable input while sending
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onEvent(ChatUiEvent.SendClicked) }, // UI sends event
                    enabled = uiState.currentInput.isNotBlank() && !uiState.isSending, // Enable only if input exists and not sending
                    modifier = Modifier.height(56.dp) // Match TextField height
                ) {
                    if (uiState.isSending) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Message")
                    }
                }
            }
        }
    }
}

@Composable
fun EducationalHeader(
    syncEnabled: Boolean, onSyncToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "Info",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "How WorkManager Sync Works",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "1. Messages are saved locally first (offline-first)\n" + "2. WorkManager periodically syncs with server\n" + "3. Status indicators show sync progress\n" + "4. Toggle sync to batch messages",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Sync Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Sync ${if (syncEnabled) "Enabled" else "Disabled"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = syncEnabled, onCheckedChange = { onSyncToggle() })
            }
        }
    }
}

@Composable
fun SyncStatusCard(syncStatus: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),

    ) {
        Row(
            modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when {
                    syncStatus.contains("Syncing") -> "⏳"
                    else -> "✓"
                }, style = MaterialTheme.typography.bodyMedium, color = when {
                    syncStatus.contains("Syncing") -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = syncStatus,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MessageBubble(message: Message, isMe: Boolean, onRetryClick: ((String) -> Unit)? = null) {
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
                            MessageStatus.SENT_OR_PENDING -> "⏳"
                            MessageStatus.SENT_TO_SERVER -> "✓"
                            MessageStatus.FAILED_TO_SEND -> "❌"
                        }
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (message.status) {
                                MessageStatus.SENT_OR_PENDING -> "Local (pending sync)"
                                MessageStatus.SENT_TO_SERVER -> "Synced to server"
                                MessageStatus.FAILED_TO_SEND -> "Failed to sync"
                            },
                            color = textColor.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodySmall
                        )

                        // Show retry button for failed messages
                        if (message.status == MessageStatus.FAILED_TO_SEND && onRetryClick != null) {
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