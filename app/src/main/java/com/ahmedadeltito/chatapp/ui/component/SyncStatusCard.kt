package com.ahmedadeltito.chatapp.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.ahmedadeltito.chatapp.util.AppConstants

@Composable
fun SyncStatusCard(syncStatus: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),

        ) {
        Row(
            modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when {
                    syncStatus.contains("Syncing") -> "⏳"
                    else -> "✓"
                }, style = MaterialTheme.typography.bodyMedium, color = when {
                    syncStatus.contains("Syncing") -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = syncStatus,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --- Preview Composables ---

@Preview(name = "Idle State", showBackground = true)
@Composable
private fun SyncStatusCardIdlePreview() {
    SyncStatusCard(syncStatus = AppConstants.DEFAULT_SYNC_STATUS)
}

@Preview(name = "Syncing", showBackground = true)
@Composable
private fun SyncStatusCardSyncingPreview() {
    SyncStatusCard(syncStatus = AppConstants.SYNCING_STATUS)
}

@Preview(name = "Last Synced", showBackground = true)
@Composable
private fun SyncStatusCardLastSyncedPreview() {
    SyncStatusCard(syncStatus = "Last synced: 2 min ago")
}

@Preview(name = "Error State", showBackground = true)
@Composable
private fun SyncStatusCardErrorPreview() {
    SyncStatusCard(syncStatus = AppConstants.SYNC_FAILED_STATUS)
}

@Preview(name = "Long Status", showBackground = true)
@Composable
private fun SyncStatusCardLongStatusPreview() {
    SyncStatusCard(syncStatus = "Last synced: 1 hour 23 minutes ago")
}