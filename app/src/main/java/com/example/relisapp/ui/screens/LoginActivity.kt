package com.example.relisapp.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relisapp.R
import com.example.relisapp.data.repository.UserRepository
import com.example.relisapp.data.local.AppDatabase
import com.example.relisapp.ui.theme.*
import com.example.relisapp.viewmodel.AuthViewModelFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.relisapp.MainActivity
import com.example.relisapp.data.local.SessionManager
import com.example.relisapp.di.ViewModelProviderFactory
import com.example.relisapp.ui.components.PasswordInput
import com.example.relisapp.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import java.security.MessageDigest

class LoginActivity : ComponentActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAnalytics = Firebase.analytics

        val factory = ViewModelProviderFactory.provideAuthViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]


        // Observe login state
        lifecycleScope.launch {
            viewModel.loginState.collect { user ->
                if (user != null) {
                    val session = SessionManager(this@LoginActivity)
                    session.saveLogin(user.userId)
                    Toast.makeText(
                        this@LoginActivity,
                        "Đăng nhập thành công! Chào ${user.username}",
                        Toast.LENGTH_SHORT
                    ).show()
                    // TODO: Navigate to MainActivity
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            }
        }

        setContent {
            ReLisAppTheme {
                LoginScreen(
                    onBackClick = { finish() },
                    onForgotPassword = {
                        // TODO: Open forgot password screen
                    },
                    onLoginClick = { identifier, password ->
                        if (identifier.isBlank() || password.isBlank()) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Vui lòng nhập đầy đủ thông tin",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val hashedPassword = hashPassword(password)
                            lifecycleScope.launch {
                                viewModel.login(identifier, hashedPassword)

                                // Check if login failed
                                kotlinx.coroutines.delay(500)
                                if (viewModel.loginState.value == null) {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Sai tên đăng nhập hoặc mật khẩu",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    },
                    onSignupClick = {
                        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                        startActivity(intent)
                    },
                    onGmailLogin = {
                        logGmailLoginClick()
                        Toast.makeText(
                            this@LoginActivity,
                            "Tính năng đang phát triển",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }

    private fun logGmailLoginClick() {
        firebaseAnalytics.logEvent("gmail_login_clicked") {
            param("screen_name", "login_screen")
            param("login_method", "gmail")
            param(FirebaseAnalytics.Param.METHOD, "gmail")
        }
    }

    // Hash password using SHA-256
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onBackClick: () -> Unit,
    onForgotPassword: () -> Unit,
    onLoginClick: (String, String) -> Unit,
    onSignupClick: () -> Unit,
    onGmailLogin: () -> Unit
) {
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = GrayText
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween   // ✅ không bao giờ bị tràn
        ) {

            // ---------- TOP CONTENT ----------
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Spacer(Modifier.height(8.dp))

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(140.dp)   // ✅ Logo cân đẹp
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    "Đăng nhập",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(24.dp))

                // Username / Phone
                OutlinedTextField(
                    value = identifier,
                    onValueChange = { identifier = it },
                    label = { Text("Tên đăng nhập hoặc Số điện thoại") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        focusedLabelColor = BluePrimary
                    )
                )

                Spacer(Modifier.height(16.dp))

                PasswordInput(
                    label = "Mật khẩu",
                    value = password,
                    onValueChange = { password = it }
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Quên mật khẩu?",
                    color = BluePrimary,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { onForgotPassword() }
                        .padding(vertical = 4.dp)
                )

                Spacer(Modifier.height(20.dp))

                // Login button
                Button(
                    onClick = { onLoginClick(identifier, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Text(
                        "ĐĂNG NHẬP",
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Divider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Divider(modifier = Modifier.weight(1f), color = GrayBorder)
                    Text(" Hoặc ", color = Color(0xFF888888), fontSize = 13.sp)
                    Divider(modifier = Modifier.weight(1f), color = GrayBorder)
                }

                Spacer(Modifier.height(20.dp))

                // Gmail login
                OutlinedButton(
                    onClick = onGmailLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = White),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        width = 1.5.dp,
                        brush = SolidColor(GrayBorder)
                    )
                ) {
                    Text(
                        "Đăng nhập bằng Gmail",
                        color = Color(0xFF444444),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            // ---------- BOTTOM SIGNUP ----------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .navigationBarsPadding(),       // ✅ chống bị cắt trên mọi máy
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Chưa có tài khoản?", color = GrayText, fontSize = 15.sp)
                Text(
                    " Đăng ký ngay",
                    color = BluePrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { onSignupClick() }
                        .padding(start = 4.dp)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    ReLisAppTheme {
        LoginScreen(
            onBackClick = {},
            onForgotPassword = {},
            onLoginClick = { _, _ -> },
            onSignupClick = {},
            onGmailLogin = {}
        )
    }
}