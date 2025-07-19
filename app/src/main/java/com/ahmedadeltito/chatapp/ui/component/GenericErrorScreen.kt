package com.ahmedadeltito.chatapp.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun GenericErrorScreen(
    message: String,
    onRetryClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Error: $message",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onRetryClick() }
        ) {
            Text("Retry")
        }
    }
}

// --- Preview Composables ---

@Preview(name = "Network Error", showBackground = true)
@Composable
private fun GenericErrorScreenNetworkErrorPreview() {
    GenericErrorScreen(
        message = "Network connection failed. Please check your internet connection and try again.",
        onRetryClick = {}
    )
}

@Preview(name = "Server Error", showBackground = true)
@Composable
private fun GenericErrorScreenServerErrorPreview() {
    GenericErrorScreen(
        message = "Server is temporarily unavailable. Please try again later.",
        onRetryClick = {}
    )
}

@Preview(name = "Short Error", showBackground = true)
@Composable
private fun GenericErrorScreenShortErrorPreview() {
    GenericErrorScreen(
        message = "Something went wrong.",
        onRetryClick = {}
    )
}

@Preview(name = "Long Error", showBackground = true)
@Composable
private fun GenericErrorScreenLongErrorPreview() {
    GenericErrorScreen(
        message = "This is a very long error message that demonstrates how the error screen handles text that might be longer than the available space. It should wrap properly and maintain good readability.",
        onRetryClick = {}
    )
}