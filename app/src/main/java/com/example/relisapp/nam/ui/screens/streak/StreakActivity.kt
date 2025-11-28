package com.example.relisapp.nam.ui.screens.streak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.relisapp.nam.ui.theme.LearnTheme

class StreakActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LearnTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    StreakScreen(
                        onNavigateBack = { finish() }
                    )
                }
            }
        }
    }
}
