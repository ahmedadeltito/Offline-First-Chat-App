package com.ahmedadeltito.chatapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.ahmedadeltito.chatapp.util.AppConstants

@Composable
fun DisabledMessageInputBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = { },
            label = { Text("Type a message") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            enabled = false
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = { },
            enabled = false,
            modifier = Modifier.height(56.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Message")
        }
    }
}

// --- Preview Composables ---

@Preview(name = "Disabled Input Bar", showBackground = true)
@Composable
private fun DisabledMessageInputBarPreview() {
    DisabledMessageInputBar()
}

@Preview(name = "Long Disabled Message", showBackground = true)
@Composable
private fun DisabledMessageInputBarLongMessagePreview() {
    DisabledMessageInputBar()
}