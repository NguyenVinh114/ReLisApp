package com.example.relisapp.nam.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.relisapp.nam.data.local.SessionManager
import com.example.relisapp.nam.di.ViewModelProviderFactory
import com.example.relisapp.nam.ui.theme.LearnTheme
import com.example.relisapp.nam.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class AdminActivity : ComponentActivity() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lấy userId đã login từ Session
        val sessionManager = SessionManager(this)
        val userId = sessionManager.getUserId()

        // Inject ViewModel qua Factory
        val factory = ViewModelProviderFactory.provideAuthViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        // UI state
        var username = "Bạn"

        lifecycleScope.launch {
            val user = viewModel.getUserById(userId)
            if (user != null) {
                username = user.username
            }
            setContent {
                LearnTheme {
                    MainScreen(
                        username = username,
                        onNameClick = {
                            startActivity(Intent(this@AdminActivity, ProfileActivity::class.java))
                        }
                    )
                }
            }
        }
    }
}

// ===============================
// COMPOSABLE UI
// ===============================

@Composable
fun MainScreen(username: String, onNameClick: () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Greeting(name = username, onNameClick = onNameClick)
        }
    }
}

@Composable
fun Greeting(name: String, onNameClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Xin chào,",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onNameClick() }
        )
    }
}

// PREVIEW
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    LearnTheme {
        MainScreen(
            username = "Nguyễn Hoàng Nam",
            onNameClick = {}
        )
    }
}
