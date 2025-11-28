package com.example.relisapp.nam.ui.screens.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.R
import com.example.relisapp.nam.di.ViewModelProviderFactory
import com.example.relisapp.nam.ui.components.inputs.ConfirmPasswordInput
import com.example.relisapp.nam.ui.components.inputs.PasswordInput
import com.example.relisapp.nam.ui.theme.BluePrimary
import com.example.relisapp.nam.ui.theme.LearnTheme
import com.example.relisapp.nam.ui.theme.White
import com.example.relisapp.nam.viewmodel.AuthViewModel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relisapp.nam.viewmodel.PasswordUpdateState

class ChangeForgotPasswordActivity : ComponentActivity() {

    private lateinit var viewModel: AuthViewModel
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lấy userId từ Intent (đã gắn sau khi verify OTP)
        userId = intent.getIntExtra("userId", -1)
        if (userId == -1) {
            Toast.makeText(this, "Phiên làm việc không hợp lệ", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Inject ViewModel
        val factory = ViewModelProviderFactory.provideAuthViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        setContent {
            LearnTheme {

                val isLoading by viewModel.isLoading.collectAsState()
                val updateState by viewModel.passwordUpdateState.collectAsState()

                // Lắng nghe trạng thái cập nhật mật khẩu
                LaunchedEffect(updateState) {
                    when (updateState) {
                        is PasswordUpdateState.Success -> {
                            Toast.makeText(
                                this@ChangeForgotPasswordActivity,
                                "Đặt lại mật khẩu thành công!",
                                Toast.LENGTH_SHORT
                            ).show()

                            viewModel.resetPasswordState()

                            // Về màn Login, clear back stack
                            val intent = Intent(
                                this@ChangeForgotPasswordActivity,
                                LoginActivity::class.java
                            ).apply {
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                            finish()
                        }

                        is PasswordUpdateState.Error -> {
                            Toast.makeText(
                                this@ChangeForgotPasswordActivity,
                                (updateState as PasswordUpdateState.Error).message,
                                Toast.LENGTH_LONG
                            ).show()
                            viewModel.resetPasswordState()
                        }

                        else -> Unit
                    }
                }

                ChangeForgotPasswordScreen(
                    isLoading = isLoading,
                    onBackClick = { finish() },
                    onSaveClick = { password, confirm ->

                        // VALIDATION
                        when {
                            password.length < 6 -> {
                                Toast.makeText(
                                    this,
                                    "Mật khẩu phải ≥ 6 ký tự",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            password != confirm -> {
                                Toast.makeText(
                                    this,
                                    "Xác nhận mật khẩu không khớp",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> {
                                // ✅ Không cần mật khẩu cũ trong flow "Quên mật khẩu"
                                viewModel.resetPasswordWithoutOld(
                                    userId = userId,
                                    newPassword = password
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeForgotPasswordScreen(
    isLoading: Boolean = false,
    onBackClick: () -> Unit,
    onSaveClick: (String, String) -> Unit
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đặt lại mật khẩu") },
                navigationIcon = {
                    IconButton(onClick = onBackClick, enabled = !isLoading) {
                        Icon(
                            painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            PasswordInput(
                label = "Mật khẩu mới",
                value = password,
                onValueChange = { password = it },
                enabled = !isLoading
            )

            Spacer(Modifier.height(16.dp))

            ConfirmPasswordInput(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                enabled = !isLoading
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { onSaveClick(password, confirmPassword) },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = White)
                } else {
                    Text("Lưu thay đổi", color = White, fontSize = 16.sp)
                }
            }
        }
    }
}
