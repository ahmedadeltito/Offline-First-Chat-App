package com.ahmedadeltito.chatapp.data.remote

import kotlinx.coroutines.delay

// Implementation of ChatApiService.
class ChatApiServiceImpl : ChatApiService {
    
    override suspend fun sendMessage(message: MessageDto): Boolean {
        // Simulate network delay
        delay(1000)
        
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
        delay(500)
        
        // Simulate fetching messages from server
        return listOf(
            MessageDto(
                id = "remote_1",
                senderId = "user1",
                text = "Hello from server!",
                timestamp = System.currentTimeMillis() - 60000 // 1 minute ago
            ),
            MessageDto(
                id = "remote_2", 
                senderId = "user2",
                text = "How are you doing?",
                timestamp = System.currentTimeMillis() - 30000 // 30 seconds ago
            )
        )
    }
} 