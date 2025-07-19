package com.ahmedadeltito.chatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.ahmedadeltito.chatapp.presentation.chat.ChatRoute
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                // Use ChatRoute which handles ViewModel creation and side effects

                // Future navigation callbacks can be added here:
                // onNavigateToSettings = { /* navigate to settings */ },
                // onNavigateToProfile = { /* navigate to profile */ }
                ChatRoute()
            }
        }
    }
}