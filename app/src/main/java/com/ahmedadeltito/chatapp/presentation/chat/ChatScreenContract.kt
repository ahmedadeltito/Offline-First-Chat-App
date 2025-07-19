package com.ahmedadeltito.chatapp.presentation.chat

// --- UI State ---
sealed interface ChatUiState {
    data class Loading(val currentUserId: String) : ChatUiState
    data class Success(
        val messages: List<ChatMessageUiModel>,
        val currentUserId: String
    ) : ChatUiState
    data class Error(
        val message: String,
        val currentUserId: String
    ) : ChatUiState
}

data class ChatUiStatus(
    val currentInput: String, // Text in the message input field
    val isSending: Boolean, // Whether a message is currently being sent
    val syncStatus: String, // Display sync status to user (e.g., "Syncing...", "Last synced: X min ago")
    val syncEnabled: Boolean // Whether sync is currently enabled or disabled
)

// --- UI Events ---
sealed interface ChatUiEvent {
    data class InputChanged(val newInput: String) : ChatUiEvent
    object SendClicked : ChatUiEvent
    data class RetryMessageClicked(val messageId: String) : ChatUiEvent
    object SyncToggleClicked : ChatUiEvent
    object RefreshClicked : ChatUiEvent // New: Manual refresh
    object ClearInputClicked : ChatUiEvent // New: Clear input field
}

// --- Side Effects ---
sealed interface ChatSideEffect {
    data class ShowSnackbar(val message: String) : ChatSideEffect
    object NavigateToSettings : ChatSideEffect
    object NavigateToProfile : ChatSideEffect
    object NavigateToChatDetails : ChatSideEffect // New: Navigate to message details
    data class ShareMessage(val messageText: String) : ChatSideEffect // New: Share message
}