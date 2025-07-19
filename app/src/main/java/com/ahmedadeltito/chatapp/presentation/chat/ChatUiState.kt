package com.ahmedadeltito.chatapp.presentation.chat

import com.ahmedadeltito.chatapp.domain.Message

// Represents the entire UI state of the chat screen
data class ChatUiState(
    val messages: List<Message> = emptyList(),  // The list of messages to display
    val currentInput: String = "",              // Text in the message input field
    val isLoading: Boolean = false,             // Whether initial messages are loading
    val error: String? = null,                  // Any error message to show
    val isSending: Boolean = false,             // Whether a message is currently being sent
    val syncStatus: String = "Idle",            // Display sync status to user (e.g., "Syncing...", "Last synced: X min ago")
    val syncEnabled: Boolean = true,            // Whether sync is currently enabled or disabled
    val currentUserId: String = "myUserId"      // Current user ID for UI logic (e.g., determining message alignment)
) 