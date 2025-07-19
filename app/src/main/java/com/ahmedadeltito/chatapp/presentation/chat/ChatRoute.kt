package com.ahmedadeltito.chatapp.presentation.chat

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ahmedadeltito.chatapp.presentation.chat.ChatSideEffect.NavigateToChatDetails
import com.ahmedadeltito.chatapp.presentation.chat.ChatSideEffect.NavigateToProfile
import com.ahmedadeltito.chatapp.presentation.chat.ChatSideEffect.NavigateToSettings
import com.ahmedadeltito.chatapp.presentation.chat.ChatSideEffect.ShareMessage
import com.ahmedadeltito.chatapp.presentation.chat.ChatSideEffect.ShowSnackbar
import com.ahmedadeltito.chatapp.ui.component.TopSnackbarHost
import kotlinx.coroutines.flow.collectLatest

/**
 * ChatRoute is responsible for:
 * 1. Creating and managing the ViewModel
 * 2. Handling side effects
 * 3. Providing the UI state to ChatScreen
 *
 * This separation allows ChatScreen to be purely UI-focused
 * and makes it easier to add navigation later.
 */
@Composable
fun ChatRoute(
    onNavigateToSettings: (() -> Unit)? = null,
    onNavigateToProfile: (() -> Unit)? = null
) {
    // Create ViewModel using Hilt
    val viewModel: ChatViewModel = hiltViewModel()

    // Collect UI state
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Collect status state
    val statusState by viewModel.statusState.collectAsStateWithLifecycle()

    // Create SnackbarHostState for side effects
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle side effects
    LaunchedEffect(Unit) {
        viewModel.sideEffects.collectLatest { sideEffect ->
            when (sideEffect) {
                is ShowSnackbar -> snackbarHostState.showSnackbar(sideEffect.message)
                is NavigateToSettings -> onNavigateToSettings?.invoke()
                is NavigateToProfile -> onNavigateToProfile?.invoke()
                is NavigateToChatDetails -> { /*Future: Navigate to message details*/ }
                is ShareMessage -> { /*Future: Share message functionality*/ }
            }
        }
    }

    // Render the ChatScreen with pure UI state
    ChatScreen(
        uiState = uiState,
        statusState = statusState,
        onEvent = viewModel::onEvent
    )

    // Show snackbar at the top of the screen
    TopSnackbarHost(hostState = snackbarHostState)
} 