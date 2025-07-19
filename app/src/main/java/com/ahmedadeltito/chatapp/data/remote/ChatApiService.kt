package com.ahmedadeltito.chatapp.data.remote

// Interface for chat API operations.
interface ChatApiService {
    suspend fun sendMessage(message: MessageDto): Boolean
    suspend fun fetchMessages(): List<MessageDto>
}