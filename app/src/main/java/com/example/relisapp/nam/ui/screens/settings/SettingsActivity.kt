package com.example.relisapp.nam.ui.screens.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.nam.di.ReminderSettingsViewModelFactory
import com.example.relisapp.nam.ui.theme.LearnTheme
import com.example.relisapp.nam.viewmodel.ReminderSettingsViewModel

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = ReminderSettingsViewModelFactory(this)
        val viewModel = ViewModelProvider(this, factory)[ReminderSettingsViewModel::class.java]

        setContent {
            LearnTheme {
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { finish() }
                )
            }
        }
    }
}
