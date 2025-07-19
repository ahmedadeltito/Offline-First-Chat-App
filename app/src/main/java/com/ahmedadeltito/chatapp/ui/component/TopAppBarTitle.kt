package com.ahmedadeltito.chatapp.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TopAppBarTitle(title: String, subtitle: String) {
    Column {
        Text(title)
        Text(
            subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

// --- Preview Composables ---

@Preview(name = "Default Title", showBackground = true)
@Composable
private fun TopAppBarTitleDefaultPreview() {
    TopAppBarTitle(
        title = "Offline-First Chat",
        subtitle = "WorkManager Demo"
    )
}

@Preview(name = "Long Title", showBackground = true)
@Composable
private fun TopAppBarTitleLongPreview() {
    TopAppBarTitle(
        title = "Very Long Application Title That Might Not Fit",
        subtitle = "This is a very long subtitle that demonstrates how the title handles long text"
    )
}

@Preview(name = "Short Title", showBackground = true)
@Composable
private fun TopAppBarTitleShortPreview() {
    TopAppBarTitle(
        title = "Chat",
        subtitle = "Demo"
    )
}