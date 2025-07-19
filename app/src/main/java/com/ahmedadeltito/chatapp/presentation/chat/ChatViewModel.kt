package com.ahmedadeltito.chatapp.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmedadeltito.chatapp.domain.ChatRepository
import com.ahmedadeltito.chatapp.domain.usecase.RetryMessageResult
import com.ahmedadeltito.chatapp.domain.usecase.RetryMessageUseCase
import com.ahmedadeltito.chatapp.domain.usecase.SendMessageResult
import com.ahmedadeltito.chatapp.domain.usecase.SendMessageUseCase
import com.ahmedadeltito.chatapp.domain.usecase.SyncStatusUseCase
import com.ahmedadeltito.chatapp.domain.usecase.ToggleSyncResult
import com.ahmedadeltito.chatapp.domain.usecase.ToggleSyncUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val sendMessageUseCase: SendMessageUseCase,
    private val retryMessageUseCase: RetryMessageUseCase,
    private val toggleSyncUseCase: ToggleSyncUseCase,
    private val syncStatusUseCase: SyncStatusUseCase
) : ViewModel() {

    // --- Current User ID (Business Logic) ---
    private val currentUserId = "myUserId" // This could come from a UserManager or AuthService in a real app

    // --- UI State ---
    private val _uiState = MutableStateFlow(
        ChatUiState(
            isLoading = true,
            currentUserId = currentUserId // Include currentUserId in initial state
        )
    )
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // --- Side Effects ---
    private val _sideEffects = MutableSharedFlow<ChatSideEffect>()
    val sideEffects = _sideEffects.asSharedFlow()

    init {
        // 1. Collect messages from the local repository for UI updates
        viewModelScope.launch {
            chatRepository.getMessages()
                .onStart { _uiState.update { it.copy(isLoading = true, error = null) } }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to load messages: ${e.message}"
                        )
                    }
                }
                .collectLatest { messages ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            messages = messages,
                            error = null
                        )
                    }
                }
        }

        // 2. Observe sync status using the use case
        viewModelScope.launch {
            syncStatusUseCase.observeSyncStatus()
                .collectLatest { syncStatus ->
                    val currentSyncEnabled = _uiState.value.syncEnabled
                    val statusText = if (!currentSyncEnabled) {
                        "Sync disabled"
                    } else {
                        syncStatus
                    }
                    _uiState.update { it.copy(syncStatus = statusText) }
                }
        }

        // 3. Initialize sync system using the use case
        viewModelScope.launch {
            syncStatusUseCase.initializeSync()
        }
    }

    // --- Event Handler (UDF Pattern) ---

    /**
     * Handles all UI events in a unidirectional data flow.
     * This is the single entry point for all user interactions.
     */
    fun onEvent(event: ChatUiEvent) {
        when (event) {
            is ChatUiEvent.InputChanged -> {
                _uiState.update { it.copy(currentInput = event.newInput) }
            }

            is ChatUiEvent.SendClicked -> {
                handleSendClicked()
            }

            is ChatUiEvent.RetryMessageClicked -> {
                handleRetryMessageClicked(event.messageId)
            }

            is ChatUiEvent.SyncToggleClicked -> {
                handleSyncToggleClicked()
            }
        }
    }

    // --- Private Event Handlers ---

    private fun handleSendClicked() {
        val messageText = _uiState.value.currentInput.trim()
        if (messageText.isBlank()) {
            viewModelScope.launch { _sideEffects.emit(ChatSideEffect.ShowSnackbar("Message cannot be empty")) }
            return
        }

        _uiState.update {
            it.copy(
                isSending = true,
                currentInput = ""
            )
        } // Clear input and show sending status

        viewModelScope.launch {
            val result = sendMessageUseCase.execute(
                text = messageText,
                senderId = currentUserId, // Use the ViewModel's currentUserId
                syncEnabled = _uiState.value.syncEnabled
            )

            _uiState.update { it.copy(isSending = false) }

            when (result) {
                is SendMessageResult.Success -> {
                    _sideEffects.emit(ChatSideEffect.ShowSnackbar(result.message))
                }

                is SendMessageResult.Error -> {
                    _sideEffects.emit(ChatSideEffect.ShowSnackbar(result.message))
                }
            }
        }
    }

    private fun handleRetryMessageClicked(messageId: String) {
        viewModelScope.launch {
            val result = retryMessageUseCase.execute(
                messageId = messageId,
                syncEnabled = _uiState.value.syncEnabled
            )

            when (result) {
                is RetryMessageResult.Success -> {
                    _sideEffects.emit(ChatSideEffect.ShowSnackbar(result.message))
                }

                is RetryMessageResult.Error -> {
                    _sideEffects.emit(ChatSideEffect.ShowSnackbar(result.message))
                }
            }
        }
    }

    private fun handleSyncToggleClicked() {
        viewModelScope.launch {
            val result = toggleSyncUseCase.execute(
                currentSyncEnabled = _uiState.value.syncEnabled
            )

            when (result) {
                is ToggleSyncResult.Success -> {
                    _uiState.update { it.copy(syncEnabled = result.newSyncEnabled) }
                    _sideEffects.emit(ChatSideEffect.ShowSnackbar(result.message))
                }

                is ToggleSyncResult.Error -> {
                    _sideEffects.emit(ChatSideEffect.ShowSnackbar(result.message))
                }
            }
        }
    }
} 