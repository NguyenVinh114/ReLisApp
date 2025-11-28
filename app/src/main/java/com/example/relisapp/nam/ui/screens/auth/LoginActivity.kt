package com.example.relisapp.nam.ui.screens.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.relisapp.MainActivity
import com.example.relisapp.R
import com.example.relisapp.nam.di.ViewModelProviderFactory
import com.example.relisapp.nam.database.entity.User
import com.example.relisapp.nam.ui.components.inputs.PasswordInput
import com.example.relisapp.nam.ui.screens.admin.AdminDashboardActivity2
import com.example.relisapp.nam.ui.theme.*
import com.example.relisapp.nam.viewmodel.AuthViewModel
import com.example.relisapp.nam.viewmodel.LoginState
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inject ViewModel
        val factory = ViewModelProviderFactory.provideAuthViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        setContent {
            LearnTheme {
                val loginState = viewModel.loginState.collectAsState()

                LoginScreen(
                    isLoading = loginState.value is LoginState.Loading,
                    onBackClick = { finish() },
                    onForgotPassword = {
                        startActivity(Intent(this, ForgotPasswordPhoneEntryActivity::class.java))
                    },
                    onLoginClick = { identifier, password ->
                        when {
                            identifier.isBlank() || password.isBlank() -> toast("Vui lòng nhập đầy đủ thông tin")
                            password.length < 6 -> toast("Mật khẩu phải ít nhất 6 ký tự")
                            else -> viewModel.login(identifier, password)
                        }
                    },
                    onSignupClick = {
                        startActivity(Intent(this, SignupActivity::class.java))
                    },
                    onGmailLogin = {
                        toast("Tính năng đang phát triển")
                    }
                )
            }
        }

        // 🔥 CHỈ 1 nơi collect loginState → KHÔNG DOUBLE EVENT
        observeLoginState()
    }

    private fun observeLoginState() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->

                when (state) {
                    LoginState.Idle -> Unit
                    LoginState.Loading -> Unit

                    is LoginState.Error -> {
                        toast(state.message)
                        viewModel.resetLoginState()
                    }

                    is LoginState.Success -> {
                        val user = state.user

                        if (user.accountStatus == "locked") {
                            toast("Tài khoản bị khóa. Liên hệ quản trị viên.")
                            viewModel.resetLoginState()
                            return@collect
                        }

                        toast("Chào ${user.username}")
                        navigateAfterLogin(user)
                    }
                }
            }
        }
    }

    private fun navigateAfterLogin(user: User) {
        val target = when (user.role?.lowercase()) {
            "admin" -> AdminDashboardActivity2::class.java
            else -> MainActivity::class.java
        }

        startActivity(
            Intent(this, target).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        finish()
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

/**
 *  Composable LoginScreen với loading state
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    isLoading: Boolean = false,
    onBackClick: () -> Unit,
    onForgotPassword: () -> Unit,
    onLoginClick: (String, String) -> Unit,
    onSignupClick: () -> Unit,
    onGmailLogin: () -> Unit
) {
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GrayText
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Spacer(Modifier.height(8.dp))

                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(140.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        "Đăng nhập",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(24.dp))

                    OutlinedTextField(
                        value = identifier,
                        onValueChange = { identifier = it },
                        label = { Text("Tên đăng nhập hoặc Số điện thoại") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            focusedLabelColor = BluePrimary
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    PasswordInput(
                        label = "Mật khẩu",
                        value = password,
                        onValueChange = { password = it },
                        enabled = !isLoading
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Quên mật khẩu?",
                        color = BluePrimary,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable(enabled = !isLoading) { onForgotPassword() }
                    )

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = { onLoginClick(identifier, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(BluePrimary)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("ĐĂNG NHẬP", color = White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    HorizontalDivider(color = GrayBorder)

                    Spacer(Modifier.height(20.dp))

                    OutlinedButton(
                        onClick = onGmailLogin,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        border = BorderStroke(1.5.dp, GrayBorder),
                        enabled = !isLoading
                    ) {
                        Text(
                            "Đăng nhập bằng Gmail",
                            color = Color(0xFF444444),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Chưa có tài khoản?", color = GrayText)
                    Text(
                        " Đăng ký ngay",
                        color = BluePrimary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { if (!isLoading) onSignupClick() }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LearnTheme {
        LoginScreen(
            onBackClick = {},
            onForgotPassword = {},
            onLoginClick = { _, _ -> },
            onSignupClick = {},
            onGmailLogin = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreenLoading() {
    LearnTheme {
        LoginScreen(
            isLoading = true,
            onBackClick = {},
            onForgotPassword = {},
            onLoginClick = { _, _ -> },
            onSignupClick = {},
            onGmailLogin = {}
        )
    }
}