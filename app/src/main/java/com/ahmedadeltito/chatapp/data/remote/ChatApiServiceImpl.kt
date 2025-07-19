package com.ahmedadeltito.chatapp.data.remote

import com.ahmedadeltito.chatapp.util.AppConstants
import kotlinx.coroutines.delay
import java.util.UUID

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
    
    override suspend fun fetchMessage(): MessageDto {
        // Simulate network delay
        delay(1000)
        val conversationMessage = generateConversationMessages(timestamp = System.currentTimeMillis()).random()
        println("ChatApiService: Fetched $conversationMessage messages from server")
        return conversationMessage
    }

    // Generate diverse, realistic conversation messages
    private fun generateConversationMessages(timestamp: Long) = listOf(
        // Initial greeting and introduction
        MessageDto(
            id = UUID.randomUUID().toString(),
            senderId = AppConstants.OTHER_USER_ID,
            text = "Hey there! 👋 How's your day going?",
            timestamp = timestamp
        ),
        MessageDto(
            id = UUID.randomUUID().toString(),
            senderId = AppConstants.OTHER_USER_ID,
            text = "That sounds fascinating! What technologies are you using?",
            timestamp = timestamp
        ),
        MessageDto(
            id = UUID.randomUUID().toString(),
            senderId = AppConstants.OTHER_USER_ID,
            text = "Wow, that's quite the tech stack! 🚀 How's the offline functionality working?",
            timestamp = timestamp
        ),
        MessageDto(
            id = UUID.randomUUID().toString(),
            senderId = AppConstants.OTHER_USER_ID,
            text = "That's exactly what modern apps need! 📱 How do you handle conflicts when the same message gets sent multiple times?",
            timestamp = timestamp
        ),
        MessageDto(
            id = UUID.randomUUID().toString(),
            senderId = AppConstants.OTHER_USER_ID,
            text = "Impressive! Are you planning to add features like message reactions or file sharing?",
            timestamp = timestamp
        ),
        MessageDto(
            id = UUID.randomUUID().toString(),
            senderId = AppConstants.OTHER_USER_ID,
            text = "That sounds like a solid foundation! 💪 How's the testing going?",
            timestamp = timestamp
        ),
        MessageDto(
            id = UUID.randomUUID().toString(),
            senderId = AppConstants.OTHER_USER_ID,
            text = "That's the way to do it! 🧪 What's the most challenging part you've encountered so far?",
            timestamp = timestamp
        ),
        MessageDto(
            id = UUID.randomUUID().toString(),
            senderId = AppConstants.OTHER_USER_ID,
            text = "WorkManager is indeed amazing for background tasks! 🔄 Are you planning to open source this project?",
            timestamp = timestamp
        ),
        MessageDto(
            id = UUID.randomUUID().toString(),
            senderId = AppConstants.OTHER_USER_ID,
            text = "That's fantastic! The Android community will definitely benefit from this. Looking forward to seeing it on GitHub! 🎉",
            timestamp = timestamp
        )
    )
} 