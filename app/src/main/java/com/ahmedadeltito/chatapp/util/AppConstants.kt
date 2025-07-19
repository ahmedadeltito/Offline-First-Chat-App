package com.ahmedadeltito.chatapp.util

// Application-wide constants.
object AppConstants {
    
    // In a real app, this would come from authentication system. For demo purposes, we use a hardcoded value.
    const val CURRENT_USER_ID = "myUserId"
    
    // This represents another user in the chat conversation.
    const val OTHER_USER_ID = "otherUser"
    
    // Sync interval in minutes for periodic background sync.
    const val SYNC_INTERVAL_MINUTES = 15L
    
    // Default sync status message when idle.
    const val DEFAULT_SYNC_STATUS = "Idle"
    
    // Sync status message when syncing.
    const val SYNCING_STATUS = "Syncing..."
    
    // Sync status message when sync fails.
    const val SYNC_FAILED_STATUS = "Sync failed"
    
    // Default error message for network issues.
    const val NETWORK_ERROR_MESSAGE = "Network connection failed. Please check your internet connection and try again."
    
    // Default error message for server issues.
    const val SERVER_ERROR_MESSAGE = "Server is temporarily unavailable. Please try again later."
    
    // Default error message for general issues.
    const val GENERAL_ERROR_MESSAGE = "Something went wrong."
    
    // Success message for message refresh.
    const val REFRESH_SUCCESS_MESSAGE = "Messages refreshed successfully"
    
    // Error message for message refresh failure.
    const val REFRESH_ERROR_MESSAGE = "Failed to refresh messages"
    
    // Error message for empty message validation.
    const val EMPTY_MESSAGE_ERROR = "Message cannot be empty"
    
    // App title for the top app bar.
    const val APP_TITLE = "Offline-First Chat"
    
    // App subtitle for the top app bar.
    const val APP_SUBTITLE = "WorkManager Demo"
    
    // Default message for empty chat state.
    const val EMPTY_CHAT_MESSAGE = "Start a conversation!"
} 