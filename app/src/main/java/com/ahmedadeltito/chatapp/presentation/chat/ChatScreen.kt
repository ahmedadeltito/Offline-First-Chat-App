package com.ahmedadeltito.chatapp.presentation.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ahmedadeltito.chatapp.presentation.chat.ChatUiEvent.ClearInputClicked
import com.ahmedadeltito.chatapp.presentation.chat.ChatUiEvent.InputChanged
import com.ahmedadeltito.chatapp.presentation.chat.ChatUiEvent.RefreshClicked
import com.ahmedadeltito.chatapp.presentation.chat.ChatUiEvent.RetryMessageClicked
import com.ahmedadeltito.chatapp.presentation.chat.ChatUiEvent.SendClicked
import com.ahmedadeltito.chatapp.presentation.chat.ChatUiEvent.SyncToggleClicked
import com.ahmedadeltito.chatapp.ui.component.EducationalHeader
import com.ahmedadeltito.chatapp.ui.component.GenericErrorScreen
import com.ahmedadeltito.chatapp.ui.component.MessageBubble
import com.ahmedadeltito.chatapp.ui.component.MessageInputBar
import com.ahmedadeltito.chatapp.ui.component.SyncStatusCard
import com.ahmedadeltito.chatapp.ui.component.TopAppBarTitle
import com.ahmedadeltito.chatapp.util.AppConstants
import java.util.Date

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
    statusState: ChatUiStatus,
    onEvent: (ChatUiEvent) -> Unit
) {
    // --- Collect UI State ---
    val listState = rememberLazyListState() // For scrolling to bottom

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TopAppBarTitle(
                        title = AppConstants.APP_TITLE,
                        subtitle = AppConstants.APP_SUBTITLE
                    )
                },
                actions = {
                    // Add refresh button
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable { onEvent(RefreshClicked) }
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- Educational Header with Sync Toggle ---
            EducationalHeader(
                syncEnabled = statusState.syncEnabled,
                onSyncToggle = { onEvent(SyncToggleClicked) }
            )

            // --- Main Content Area ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (uiState) {
                    is ChatUiState.Loading -> CircularProgressIndicator()
                    is ChatUiState.Error -> GenericErrorScreen(
                        message = uiState.message,
                        onRetryClick = { onEvent(RefreshClicked) }
                    )

                    is ChatUiState.Success -> if (uiState.messages.isEmpty()) {
                        Text(AppConstants.EMPTY_CHAT_MESSAGE)
                    } else {
                        // --- Auto-scroll to bottom when new messages arrive ---
                        LaunchedEffect(uiState.messages.size) {
                            listState.animateScrollToItem(uiState.messages.lastIndex)
                        }
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.messages, key = { it.id }) { message ->
                                MessageBubble(
                                    message = message,
                                    isMe = message.isSentByMe,
                                    onRetryClick = {
                                        onEvent(RetryMessageClicked(messageId = it))
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // --- Enhanced Sync Status Indicator ---
            SyncStatusCard(syncStatus = statusState.syncStatus)

            // --- Message Input Bar ---
            MessageInputBar(
                currentInput = statusState.currentInput,
                isSending = statusState.isSending,
                onInputChange = { onEvent(InputChanged(newInput = it)) },
                onClearInputClick = { onEvent(ClearInputClicked) },
                onSendClick = { onEvent(SendClicked) }
            )
        }
    }
}

// --- Preview Composables ---

@Preview(name = "Loading State", showBackground = true)
@Composable
private fun ChatScreenLoadingPreview() {
    ChatScreen(
        uiState = ChatUiState.Loading(currentUserId = AppConstants.CURRENT_USER_ID),
        statusState = ChatUiStatus(
            currentInput = "",
            isSending = false,
            syncStatus = AppConstants.DEFAULT_SYNC_STATUS,
            syncEnabled = true
        ),
        onEvent = {}
    )
}

@Preview(name = "Error State", showBackground = true)
@Composable
private fun ChatScreenErrorPreview() {
    ChatScreen(
        uiState = ChatUiState.Error(
            message = AppConstants.NETWORK_ERROR_MESSAGE,
            currentUserId = AppConstants.CURRENT_USER_ID
        ),
        statusState = ChatUiStatus(
            currentInput = "",
            isSending = false,
            syncStatus = AppConstants.SYNC_FAILED_STATUS,
            syncEnabled = false
        ),
        onEvent = {}
    )
}

@Preview(name = "Empty Success State", showBackground = true)
@Composable
private fun ChatScreenEmptySuccessPreview() {
    ChatScreen(
        uiState = ChatUiState.Success(
            messages = emptyList(),
            currentUserId = AppConstants.CURRENT_USER_ID
        ),
        statusState = ChatUiStatus(
            currentInput = "Hello world!",
            isSending = false,
            syncStatus = "Last synced: 2 min ago",
            syncEnabled = true
        ),
        onEvent = {}
    )
}

@Preview(name = "Success State with Messages", showBackground = true)
@Composable
private fun ChatScreenSuccessWithMessagesPreview() {
    val sampleMessages = listOf(
        MessageUiModel(
            id = "1",
            text = "Hey there! How are you doing?",
            senderId = AppConstants.OTHER_USER_ID,
            timestamp = Date(System.currentTimeMillis() - 300000),
            isSentByMe = false,
            status = MessageStatusUiModel.SENT_TO_SERVER
        ),
        MessageUiModel(
            id = "2",
            text = "I'm doing great! Just working on this offline-first chat app.",
            senderId = AppConstants.CURRENT_USER_ID,
            timestamp = Date(System.currentTimeMillis() - 240000),
            isSentByMe = true,
            status = MessageStatusUiModel.SENT_TO_SERVER
        ),
        MessageUiModel(
            id = "3",
            text = "That sounds interesting! Tell me more about it.",
            senderId = AppConstants.OTHER_USER_ID,
            timestamp = Date(System.currentTimeMillis() - 180000),
            isSentByMe = false,
            status = MessageStatusUiModel.SENT_TO_SERVER
        ),
        MessageUiModel(
            id = "4",
            text = "It uses WorkManager for background sync and Room for local storage. Pretty cool stuff!",
            senderId = AppConstants.CURRENT_USER_ID,
            timestamp = Date(System.currentTimeMillis() - 120000),
            isSentByMe = true,
            status = MessageStatusUiModel.SENT_OR_PENDING
        )
    )

    ChatScreen(
        uiState = ChatUiState.Success(
            messages = sampleMessages,
            currentUserId = AppConstants.CURRENT_USER_ID
        ),
        statusState = ChatUiStatus(
            currentInput = "Thanks for asking!",
            isSending = true,
            syncStatus = AppConstants.SYNCING_STATUS,
            syncEnabled = true
        ),
        onEvent = {}
    )
}