package com.ahmedadeltito.chatapp.data.remote

import com.ahmedadeltito.chatapp.data.remote.MessageDto

/**
 * Interface for chat API operations.
 * This allows for better testing and abstraction.
 */
interface ChatApiService {
    suspend fun sendMessage(message: MessageDto): Boolean
    suspend fun fetchMessages(): List<MessageDto>
}

/**
 * Implementation of ChatApiService.
 * Simulates network operations with delays and potential failures.
 */
class ChatApiServiceImpl : ChatApiService {
    
    override suspend fun sendMessage(message: MessageDto): Boolean {
        // Simulate network delay
        kotlinx.coroutines.delay(1000)
        
        // Simulate occasional network failures (20% failure rate)
        if (Math.random() < 0.2) {
            println("ChatApiService: Simulated network failure for message: ${message.text}")
            throw Exception("Network error")
        }
        
        println("ChatApiService: Successfully sent message to server: ${message.text}")
        return true
    }
    
    override suspend fun fetchMessages(): List<MessageDto> {
        // Simulate network delay
        kotlinx.coroutines.delay(500)
        
        // Simulate fetching messages from server
        return listOf(
            MessageDto(
                id = "remote_1",
                senderId = "otherUser",
                text = "Hello from server!",
                timestamp = System.currentTimeMillis() - 60000 // 1 minute ago
            ),
            MessageDto(
                id = "remote_2", 
                senderId = "otherUser",
                text = "How are you doing?",
                timestamp = System.currentTimeMillis() - 30000 // 30 seconds ago
            )
        )
    }
} 