package com.example.relisapp.nam.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.relisapp.nam.MainActivity
import com.example.relisapp.R
import com.example.relisapp.nam.di.ViewModelProviderFactory
import com.example.relisapp.nam.database.entity.User
import com.example.relisapp.nam.ui.components.PasswordInput
import com.example.relisapp.nam.ui.theme.*
import com.example.relisapp.nam.viewmodel.AuthViewModel
import com.example.relisapp.nam.viewmodel.LoginState
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAnalytics = Firebase.analytics

        // Inject ViewModel
        val factory = ViewModelProviderFactory.provideAuthViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        // Reset state ban đầu
        viewModel.resetLoginState()

        setContent {
            LearnTheme {
                // ✅ FIX LỖI 3: Collect loginState để lấy isLoading
                val loginState by viewModel.loginState.collectAsState()
                val isLoading = loginState is LoginState.Loading

                // ✅ FIX LỖI 1: Dùng repeatOnLifecycle thay vì collect trực tiếp
                LaunchedEffect(Unit) {
                    lifecycleScope.launch {
                        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.loginState.collect { state ->
                                handleLoginState(state)
                            }
                        }
                    }
                }

                LoginScreen(
                    isLoading = isLoading, // ✅ Truyền loading state
                    onBackClick = { finish() },
                    onForgotPassword = {
                        startActivity(Intent(this, ForgotPasswordPhoneEntryActivity::class.java))
                    },
                    onLoginClick = { identifier, password ->
                        // ✅ FIX LỖI 5: Validation input đầy đủ hơn
                        when {
                            identifier.isBlank() || password.isBlank() -> {
                                Toast.makeText(
                                    this,
                                    "Vui lòng nhập đầy đủ thông tin",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            password.length < 6 -> {
                                Toast.makeText(
                                    this,
                                    "Mật khẩu phải có ít nhất 6 ký tự",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> {
                                viewModel.login(identifier, password)
                            }
                        }
                    },
                    onSignupClick = {
                        startActivity(Intent(this, SignupActivity::class.java))
                    },
                    onGmailLogin = {
                        logGmailLoginClick()
                        Toast.makeText(
                            this,
                            "Tính năng đang phát triển",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }

    // ✅ FIX LỖI 7: Reset state khi resume để tránh state cũ
    override fun onResume() {
        super.onResume()
        viewModel.resetLoginState()
    }

    /**
     * ✅ Xử lý các trạng thái login một cách rõ ràng
     */
    private fun handleLoginState(state: LoginState) {
        when (state) {
            LoginState.Idle -> {
                // Không làm gì
            }

            LoginState.Loading -> {
                // UI đã xử lý loading indicator
            }

            is LoginState.Error -> {
                Toast.makeText(
                    this,
                    state.message,
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.resetLoginState()
            }

            is LoginState.Success -> {
                val user = state.user

                // 🔒 CHẶN USER BỊ KHÓA
                if (user.accountStatus == "locked") {
                    Toast.makeText(
                        this,
                        "Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên!",
                        Toast.LENGTH_LONG
                    ).show()

                    viewModel.resetLoginState()
                    return
                }

                Toast.makeText(
                    this,
                    "Đăng nhập thành công! Chào ${user.username}",
                    Toast.LENGTH_SHORT
                ).show()

                navigateAfterLogin(user)
            }

        }
    }

    /**
     * ✅ FIX LỖI 2: Điều hướng dựa trên role của user
     */
    private fun navigateAfterLogin(user: User) {
        val targetActivity = when (user.role?.lowercase()) {
            "admin" -> AdminDashboardActivity2::class.java
            else -> MainActivity::class.java
        }

        val intent = Intent(this, targetActivity).apply {
            // Clear back stack để user không back lại màn login
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        startActivity(intent)
        finish()
    }

    private fun logGmailLoginClick() {
        firebaseAnalytics.logEvent("gmail_login_clicked") {
            param("screen_name", "login_screen")
            param("login_method", "gmail")
            param(FirebaseAnalytics.Param.METHOD, "gmail")
        }
    }
}

/**
 * ✅ Composable LoginScreen với loading state
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    isLoading: Boolean = false, // ✅ FIX LỖI 3: Nhận loading state
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
                    .padding(horizontal = 24.dp)
                    .background(MaterialTheme.colorScheme.background),
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
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(Modifier.height(24.dp))

                    OutlinedTextField(
                        value = identifier,
                        onValueChange = { identifier = it },
                        label = { Text("Tên đăng nhập hoặc Số điện thoại") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading, // ✅ Disable khi loading
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
                        enabled = !isLoading // ✅ Disable khi loading
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Quên mật khẩu?",
                        color = BluePrimary,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable(enabled = !isLoading) { onForgotPassword() }
                            .padding(vertical = 4.dp)
                    )

                    Spacer(Modifier.height(20.dp))

                    // ✅ FIX LỖI 4: Disable button khi đang loading
                    Button(
                        onClick = { onLoginClick(identifier, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                        enabled = !isLoading // ✅ Ngăn spam click
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "ĐĂNG NHẬP",
                                color = White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
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
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.5.dp, SolidColor(GrayBorder)),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = White),
                        enabled = !isLoading // ✅ Disable khi loading
                    ) {
                        Text(
                            "Đăng nhập bằng Gmail",
                            color = Color(0xFF444444),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Chưa có tài khoản?", color = GrayText, fontSize = 15.sp)
                    Text(
                        " Đăng ký ngay",
                        color = BluePrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable(enabled = !isLoading) { onSignupClick() }
                            .padding(start = 4.dp)
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