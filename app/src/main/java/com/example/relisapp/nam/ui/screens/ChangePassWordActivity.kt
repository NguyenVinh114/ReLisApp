package com.example.relisapp.nam.ui.screens

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.nam.di.ViewModelProviderFactory
import com.example.relisapp.nam.ui.theme.LearnTheme
import com.example.relisapp.nam.viewmodel.AuthViewModel
import com.example.relisapp.nam.data.local.SessionManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relisapp.R
import com.example.relisapp.nam.ui.components.PasswordInput
import com.example.relisapp.nam.ui.components.ConfirmPasswordInput
import com.example.relisapp.nam.ui.theme.BluePrimary
import androidx.compose.ui.graphics.Color
import com.example.relisapp.nam.viewmodel.PasswordUpdateState

class ChangePassWordActivity : ComponentActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        session = SessionManager(this)
        val userId = session.getUserId()

        // Kiểm tra session
        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Inject ViewModel
        val factory = ViewModelProviderFactory.provideAuthViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        // Load user
        viewModel.loadUser(userId)

        setContent {
            LearnTheme {

                val user by viewModel.currentUser.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()
                val updateState by viewModel.passwordUpdateState.collectAsState()

                // Xử lý trạng thái update
                LaunchedEffect(updateState) {
                    when (updateState) {

                        is PasswordUpdateState.Success -> {
                            Toast.makeText(
                                this@ChangePassWordActivity,
                                "Đổi mật khẩu thành công!",
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.resetPasswordState()
                            finish()
                        }

                        is PasswordUpdateState.Error -> {
                            Toast.makeText(
                                this@ChangePassWordActivity,
                                (updateState as PasswordUpdateState.Error).message,
                                Toast.LENGTH_LONG
                            ).show()
                            viewModel.resetPasswordState()
                        }

                        else -> {}
                    }
                }

                ChangePasswordScreen(
                    isLoading = isLoading,
                    onBackClick = { finish() },
                    onSaveClick = { oldPw, newPw, confirmPw ->

                        if (user == null) {
                            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show()
                            return@ChangePasswordScreen
                        }

                        // Validate
                        when {
                            oldPw.isBlank() -> {
                                Toast.makeText(this, "Vui lòng nhập mật khẩu cũ", Toast.LENGTH_SHORT).show()
                            }
                            newPw.length < 6 -> {
                                Toast.makeText(this, "Mật khẩu mới phải >= 6 ký tự", Toast.LENGTH_SHORT).show()
                            }
                            newPw != confirmPw -> {
                                Toast.makeText(this, "Xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                viewModel.updatePasswordSecure(
                                    oldPassword = oldPw,
                                    newPassword = newPw,
                                    currentUser = user!!
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
fun ChangePasswordScreen(
    isLoading: Boolean = false,
    onBackClick: () -> Unit,
    onSaveClick: (String, String, String) -> Unit
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thay đổi mật khẩu") },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        enabled = !isLoading
                    ) {
                        Icon(
                            painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(20.dp))

            PasswordInput(
                label = "Nhập mật khẩu cũ",
                value = oldPassword,
                onValueChange = { oldPassword = it },
                enabled = !isLoading
            )

            Spacer(Modifier.height(24.dp))

            PasswordInput(
                label = "Nhập mật khẩu mới",
                value = newPassword,
                onValueChange = { newPassword = it },
                enabled = !isLoading
            )

            Spacer(Modifier.height(24.dp))

            ConfirmPasswordInput(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                enabled = !isLoading
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { onSaveClick(oldPassword, newPassword, confirmPassword) },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Lưu",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewChangePassword() {
    LearnTheme {
        ChangePasswordScreen(
            onBackClick = {},
            onSaveClick = { _, _, _ -> }
        )
    }
}
