package com.example.relisapp.nam.ui.screens.leaderboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.nam.data.local.SessionManager
import com.example.relisapp.nam.data.repository.UserRepository
import com.example.relisapp.nam.database.AppDatabase
import com.example.relisapp.nam.ui.theme.LearnTheme
import com.example.relisapp.nam.viewmodel.LeaderboardViewModel
import com.example.relisapp.nam.viewmodel.LeaderboardViewModelFactory

class LeaderboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(this)
        val repo = UserRepository(db.userDao())
        val session = SessionManager(this)

        val factory = LeaderboardViewModelFactory(repo)
        val viewModel = ViewModelProvider(this, factory)[LeaderboardViewModel::class.java]

        val currentUserId = session.getUserId()

        setContent {
            LearnTheme {
                LeaderboardScreen(
                    viewModel = viewModel,
                    currentUserId = currentUserId,
                    onBack = { finish() }
                )
            }
        }
    }
}