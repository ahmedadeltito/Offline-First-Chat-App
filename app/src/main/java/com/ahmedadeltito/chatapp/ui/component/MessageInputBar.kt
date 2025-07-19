package com.ahmedadeltito.chatapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MessageInputBar(
    currentInput: String,
    isSending: Boolean,
    onInputChange: (String) -> Unit,
    onClearInputClick: () -> Unit,
    onSendClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = currentInput,
            onValueChange = { newInput -> onInputChange(newInput) },
            label = { Text("Type a message") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            enabled = !isSending
        )

        // Clear input button
        if (currentInput.isNotBlank()) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.Clear,
                contentDescription = "Clear input",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onClearInputClick() }
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = { onSendClick() },
            enabled = currentInput.isNotBlank() && !isSending,
            modifier = Modifier.height(56.dp)
        ) {
            if (isSending) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Message")
            }
        }
    }
}

// --- Preview Composables ---

@Preview(name = "Empty Input", showBackground = true)
@Composable
private fun MessageInputBarEmptyPreview() {
    MessageInputBar(
        currentInput = "",
        isSending = false,
        onInputChange = {},
        onClearInputClick = {},
        onSendClick = {}
    )
}

@Preview(name = "With Text", showBackground = true)
@Composable
private fun MessageInputBarWithTextPreview() {
    MessageInputBar(
        currentInput = "Hello, this is a sample message!",
        isSending = false,
        onInputChange = {},
        onClearInputClick = {},
        onSendClick = {}
    )
}

@Preview(name = "Sending State", showBackground = true)
@Composable
private fun MessageInputBarSendingPreview() {
    MessageInputBar(
        currentInput = "This message is being sent...",
        isSending = true,
        onInputChange = {},
        onClearInputClick = {},
        onSendClick = {}
    )
}

@Preview(name = "Long Text", showBackground = true)
@Composable
private fun MessageInputBarLongTextPreview() {
    MessageInputBar(
        currentInput = "This is a very long message that demonstrates how the input bar handles text that might be longer than the available space. It should wrap properly and maintain good UX.",
        isSending = false,
        onInputChange = {},
        onClearInputClick = {},
        onSendClick = {}
    )
}