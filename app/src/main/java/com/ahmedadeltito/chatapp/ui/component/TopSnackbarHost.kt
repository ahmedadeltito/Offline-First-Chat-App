package com.ahmedadeltito.chatapp.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Custom SnackbarHost that displays snackbars at the top of the screen
 * instead of the bottom to avoid covering input fields.
 */
@Composable
fun TopSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(top = 100.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        SnackbarHost(
            hostState = hostState,
            modifier = Modifier.padding(16.dp)
        )
    }
}