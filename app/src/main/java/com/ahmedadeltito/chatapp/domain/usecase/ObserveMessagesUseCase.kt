package com.ahmedadeltito.chatapp.domain.usecase

import com.ahmedadeltito.chatapp.domain.ChatRepository
import com.ahmedadeltito.chatapp.domain.Message
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Use case for observing messages from the repository.
class ObserveMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(): Flow<List<Message>> = chatRepository.getMessages()
} 