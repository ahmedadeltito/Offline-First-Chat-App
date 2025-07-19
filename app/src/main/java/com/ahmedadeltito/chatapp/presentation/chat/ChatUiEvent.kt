package com.ahmedadeltito.chatapp.presentation.chat

/**
 * Represents all possible UI events that can occur in the chat screen.
 * This follows the Unidirectional Data Flow (UDF) pattern.
 */
sealed class ChatUiEvent {
    
    /**
     * User typed in the message input field
     */
    data class InputChanged(val newInput: String) : ChatUiEvent()
    
    /**
     * User clicked the send button
     */
    object SendClicked : ChatUiEvent()
    
    /**
     * User clicked retry on a failed message
     */
    data class RetryMessageClicked(val messageId: String) : ChatUiEvent()
    
    /**
     * User toggled the sync switch
     */
    object SyncToggleClicked : ChatUiEvent()
} 