package com.ahmedadeltito.chatapp.domain.usecase

import com.ahmedadeltito.chatapp.domain.ChatRepository
import javax.inject.Inject

// Use case for refreshing messages from the remote server.
class RefreshMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke() { chatRepository.fetchAndSyncRemoteMessages() }
} 