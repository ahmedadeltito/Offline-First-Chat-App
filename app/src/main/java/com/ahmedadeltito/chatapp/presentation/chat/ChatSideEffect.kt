package com.ahmedadeltito.chatapp.presentation.chat

/**
 * Represents side effects that can be triggered by the ViewModel.
 * These are one-time events that should be handled by the UI layer.
 */
sealed class ChatSideEffect {
    
    /**
     * Show a snackbar with the given message
     */
    data class ShowSnackbar(val message: String) : ChatSideEffect()
    
    /**
     * Navigate to settings screen
     */
    object NavigateToSettings : ChatSideEffect()
    
    /**
     * Navigate to profile screen
     */
    object NavigateToProfile : ChatSideEffect()
}