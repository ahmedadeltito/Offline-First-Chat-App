package com.ahmedadeltito.chatapp.presentation.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    
    // Create SnackbarHostState for side effects
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
    
    // Handle side effects
    LaunchedEffect(Unit) {
        viewModel.sideEffects.collectLatest { sideEffect ->
            when (sideEffect) {
                is ChatSideEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(sideEffect.message)
                }
                is ChatSideEffect.NavigateToSettings -> {
                    onNavigateToSettings?.invoke()
                }
                is ChatSideEffect.NavigateToProfile -> {
                    onNavigateToProfile?.invoke()
                }
            }
        }
    }
    
    // Render the ChatScreen with pure UI state
    ChatScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState
    )
} 