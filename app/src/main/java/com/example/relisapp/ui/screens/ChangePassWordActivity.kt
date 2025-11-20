package com.example.relisapp.ui.screens

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.relisapp.di.ViewModelProviderFactory
import com.example.relisapp.ui.theme.ReLisAppTheme
import com.example.relisapp.viewmodel.AuthViewModel
import com.example.relisapp.data.local.SessionManager
import kotlinx.coroutines.launch
import java.security.MessageDigest
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
import com.example.relisapp.ui.components.PasswordInput
import com.example.relisapp.ui.components.ConfirmPasswordInput
import com.example.relisapp.ui.theme.BluePrimary
import androidx.compose.ui.graphics.Color



class ChangePasswordActivity : ComponentActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = ViewModelProviderFactory.provideAuthViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
        session = SessionManager(this)

        setContent {
            ReLisAppTheme {
                ChangePasswordScreen(
                    onBackClick = { finish() },
                    onSaveClick = { oldPw, newPw, confirmPw ->
                        handleChange(oldPw, newPw, confirmPw)
                    }
                )
            }
        }
    }

    private fun handleChange(oldPw: String, newPw: String, confirmPw: String) {
        val userId = session.getUserId()

        lifecycleScope.launch {
            val user = viewModel.getUserById(userId)

            if (user == null) {
                Toast.makeText(this@ChangePasswordActivity, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show()
                return@launch
            }

            if (hash(oldPw) != user.passwordHash) {
                Toast.makeText(this@ChangePasswordActivity, "Mật khẩu cũ không đúng", Toast.LENGTH_SHORT).show()
                return@launch
            }

            if (newPw.length < 6) {
                Toast.makeText(this@ChangePasswordActivity, "Mật khẩu mới quá ngắn", Toast.LENGTH_SHORT).show()
                return@launch
            }

            if (newPw != confirmPw) {
                Toast.makeText(this@ChangePasswordActivity, "Xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show()
                return@launch
            }

            viewModel.updatePassword(userId, hash(newPw))
            Toast.makeText(this@ChangePasswordActivity, "Thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun hash(text: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(text.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
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
                    IconButton(onClick = onBackClick) {
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
                onValueChange = { oldPassword = it }
            )

            Spacer(Modifier.height(24.dp))

            PasswordInput(
                label = "Nhập mật khẩu mới",
                value = newPassword,
                onValueChange = { newPassword = it }
            )

            Spacer(Modifier.height(24.dp))

            ConfirmPasswordInput(
                value = confirmPassword,
                onValueChange = { confirmPassword = it }
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { onSaveClick(oldPassword, newPassword, confirmPassword) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                Text(
                    "Lưu",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewChangePassword() {
    ReLisAppTheme {
        ChangePasswordScreen(
            onBackClick = {},
            onSaveClick = { _, _, _ -> }
        )
    }
}

