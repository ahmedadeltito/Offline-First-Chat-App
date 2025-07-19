package com.ahmedadeltito.chatapp.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmedadeltito.chatapp.domain.usecase.InitializeSyncUseCase
import com.ahmedadeltito.chatapp.domain.usecase.ObserveMessagesUseCase
import com.ahmedadeltito.chatapp.domain.usecase.RefreshMessagesUseCase
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
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val refreshMessagesUseCase: RefreshMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val retryMessageUseCase: RetryMessageUseCase,
    private val toggleSyncUseCase: ToggleSyncUseCase,
    private val syncStatusUseCase: SyncStatusUseCase,
    private val initializeSyncUseCase: InitializeSyncUseCase
) : ViewModel() {

    // --- Current User ID (Business Logic) ---
    // This could come from a UserManager or AuthService in a real app
    private val currentUserId = "myUserId"

    // --- UI State ---
    private val _uiState = MutableStateFlow<ChatUiState>(
        ChatUiState.Loading(currentUserId = currentUserId)
    )
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // --- Status State (separate from main UI state) ---
    private val _statusState = MutableStateFlow(
        ChatUiStatus(
            currentInput = "",
            isSending = false,
            syncStatus = "Idle",
            syncEnabled = true
        )
    )
    val statusState: StateFlow<ChatUiStatus> = _statusState.asStateFlow()

    // --- Side Effects ---
    private val _sideEffects = MutableSharedFlow<ChatSideEffect>()
    val sideEffects = _sideEffects.asSharedFlow()

    init {
        // Initialize the chat system
        initializeChatSystem()
    }

    // --- Private Initialization Functions ---

    // Initializes the chat system by setting up message observation, sync status observation, and initial sync.
    private fun initializeChatSystem() {
        setupMessageObservation()
        setupSyncStatusObservation()
        initializeSyncSystem()
    }

    /**
     * Sets up message observation from the repository.
     */
    private fun setupMessageObservation() {
        viewModelScope.launch {
            observeMessagesUseCase()
                .onStart { _uiState.update { ChatUiState.Loading(currentUserId = currentUserId) } }
                .catch { e ->
                    _uiState.update {
                        ChatUiState.Error(
                            message = "Failed to load messages: ${e.message}",
                            currentUserId = currentUserId
                        )
                    }
                }
                .collectLatest { messages ->
                    val uiMessages = messages.map { it.toUiModel() }
                    _uiState.update {
                        ChatUiState.Success(
                            messages = uiMessages,
                            currentUserId = currentUserId
                        )
                    }
                }
        }
    }

    // Sets up sync status observation.
    private fun setupSyncStatusObservation() {
        viewModelScope.launch {
            syncStatusUseCase.observeSyncStatus()
                .collectLatest { syncStatus ->
                    _statusState.update { currentStatus ->
                        currentStatus.copy(syncStatus = syncStatus)
                    }
                }
        }
    }

    // Initializes the sync system.
    private fun initializeSyncSystem() {
        viewModelScope.launch { initializeSyncUseCase() }
    }

    // --- Event Handler (UDF Pattern) ---
    fun onEvent(event: ChatUiEvent) {
        when (event) {
            is ChatUiEvent.InputChanged -> handleInputChanged(event.newInput)
            is ChatUiEvent.SendClicked -> handleSendClicked()
            is ChatUiEvent.RetryMessageClicked -> handleRetryMessageClicked(event.messageId)
            is ChatUiEvent.SyncToggleClicked -> handleSyncToggleClicked()
            is ChatUiEvent.RefreshClicked -> handleRefreshClicked()
            is ChatUiEvent.ClearInputClicked -> handleClearInputClicked()
        }
    }

    // --- Private Event Handlers ---

    private fun handleInputChanged(newInput: String) {
        _statusState.update { currentStatus -> currentStatus.copy(currentInput = newInput) }
    }

    private fun handleSendClicked() {
        val currentStatus = _statusState.value
        val messageText = currentStatus.currentInput.trim()

        if (messageText.isBlank()) {
            viewModelScope.launch {
                _sideEffects.emit(ChatSideEffect.ShowSnackbar("Message cannot be empty"))
            }
            return
        }

        _statusState.update { it.copy(isSending = true, currentInput = "") }

        viewModelScope.launch {
            val result = sendMessageUseCase(
                text = messageText,
                senderId = currentUserId,
                syncEnabled = currentStatus.syncEnabled
            )

            _statusState.update { it.copy(isSending = false) }

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
        val currentStatus = _statusState.value

        viewModelScope.launch {
            val result = retryMessageUseCase(
                messageId = messageId,
                syncEnabled = currentStatus.syncEnabled
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
        val currentStatus = _statusState.value

        viewModelScope.launch {
            val result = toggleSyncUseCase(
                currentSyncEnabled = currentStatus.syncEnabled
            )

            when (result) {
                is ToggleSyncResult.Success -> {
                    _statusState.update { it.copy(syncEnabled = result.newSyncEnabled) }
                    _sideEffects.emit(ChatSideEffect.ShowSnackbar(result.message))
                }
                is ToggleSyncResult.Error -> {
                    _sideEffects.emit(ChatSideEffect.ShowSnackbar(result.message))
                }
            }
        }
    }

    private fun handleRefreshClicked() {
        viewModelScope.launch {
            try {
                refreshMessagesUseCase()
                _sideEffects.emit(ChatSideEffect.ShowSnackbar("Messages refreshed successfully"))
            } catch (e: Exception) {
                _sideEffects.emit(ChatSideEffect.ShowSnackbar("Failed to refresh messages: ${e.message}"))
            }
        }
    }

    private fun handleClearInputClicked() {
        _statusState.update { it.copy(currentInput = "") }
    }
} 