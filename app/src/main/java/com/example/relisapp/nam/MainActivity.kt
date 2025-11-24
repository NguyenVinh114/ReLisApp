package com.example.relisapp.nam

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.relisapp.nam.data.local.SessionManager
import com.example.relisapp.nam.di.ViewModelProviderFactory
import com.example.relisapp.nam.ui.screens.HomeScreen
import com.example.relisapp.nam.ui.screens.ProfileActivity
import com.example.relisapp.nam.ui.theme.LearnTheme
import com.example.relisapp.nam.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import kotlin.jvm.java

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sessionManager = SessionManager(this)
        val userId = sessionManager.getUserId()

        // Inject ViewModel
        val factory = ViewModelProviderFactory.provideAuthViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        lifecycleScope.launch {
            val user = viewModel.getUserById(userId)
            val username = user?.username ?: "Bạn"

            setContent {
                LearnTheme {
                    HomeScreen(
                        onListeningClick = {},
                        onReadingClick = {},
                        onProgressClick = {},
                        onSearchClick = {},
                        onFavoriteClick = {},
                        onProfileClick = {
                            startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
                        }
                    )
                }
            }
        }
    }
}
