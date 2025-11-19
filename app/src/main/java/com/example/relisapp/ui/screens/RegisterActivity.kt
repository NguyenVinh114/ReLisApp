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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.relisapp.ui.screens.VerificationActivity
import com.example.relisapp.R
import com.example.relisapp.data.repository.UserRepository
import com.example.relisapp.data.local.AppDatabase
import com.example.relisapp.di.ViewModelProviderFactory
import com.example.relisapp.ui.components.ConfirmPasswordInput
import com.example.relisapp.ui.theme.*
import com.example.relisapp.viewmodel.AuthViewModel
import com.example.relisapp.viewmodel.AuthViewModelFactory
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import com.example.relisapp.ui.components.PasswordInput

class RegisterActivity : ComponentActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var auth: FirebaseAuth
    private var isLoading by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = ViewModelProviderFactory.provideAuthViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        auth = FirebaseAuth.getInstance()

        setContent {
            ReLisAppTheme {
                SignupScreen(
                    isLoading = isLoading,
                    onBackClick = {
                        RegistrationDataHolder.clear()
                        finish() },
                    onLoginClick = { RegistrationDataHolder.clear()
                        finish() },
                    onSignupClick = { username, phone, password, confirmPassword ->
                        handleSignup(username, phone, password, confirmPassword)
                    }
                )
            }
        }
    }

    private fun handleSignup(username: String, phone: String, password: String, confirmPassword: String) {
        val error = validateInputs(username, phone, password, confirmPassword)
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            return
        }

        isLoading = true
        lifecycleScope.launch {
            try {
                val existingUser = viewModel.checkUserExists(username, phone)
                if (existingUser != null) {
                    isLoading = false
                    Toast.makeText(
                        this@RegisterActivity,
                        "Tên người dùng hoặc số điện thoại đã tồn tại",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    sendOtpWithFirebase(username, phone, password)
                }
            } catch (e: Exception) {
                isLoading = false
                Toast.makeText(this@RegisterActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputs(username: String, phone: String, password: String, confirmPassword: String): String? {
        return when {
            username.isBlank() -> "Vui lòng nhập tên người dùng"
            username.length < 3 -> "Tên người dùng phải có ít nhất 3 ký tự"
            phone.isBlank() -> "Vui lòng nhập số điện thoại"
            !isValidVietnamesePhone(phone) -> "Số điện thoại không hợp lệ"
            password.length < 6 -> "Mật khẩu phải có ít nhất 6 ký tự"
            password != confirmPassword -> "Mật khẩu xác nhận không khớp"
            else -> null
        }
    }

    private fun isValidVietnamesePhone(phone: String): Boolean {
        val clean = phone.removePrefix("+84").removePrefix("0")
        return clean.matches(Regex("^[0-9]{9,10}$"))
    }

    private fun sendOtpWithFirebase(username: String, phone: String, password: String) {
        val phoneNumber = if (phone.startsWith("+")) phone else "+84${phone.removePrefix("0")}"

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: com.google.firebase.auth.PhoneAuthCredential) {
                    isLoading = false
                    Toast.makeText(this@RegisterActivity, "Xác thực tự động hoàn tất", Toast.LENGTH_SHORT).show()
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    isLoading = false
                    Toast.makeText(
                        this@RegisterActivity,
                        "Gửi OTP thất bại: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    isLoading = false
                    RegistrationDataHolder.setData(
                        username = username,
                        phone = phoneNumber,
                        passwordHash = hashPassword(password),
                        verificationId = verificationId,
                        resendToken = token
                    )
                    startActivity(Intent(this@RegisterActivity, VerificationActivity::class.java))
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

object RegistrationDataHolder {
    private var username: String? = null
    private var phone: String? = null
    private var passwordHash: String? = null
    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    fun setData(
        username: String,
        phone: String,
        passwordHash: String,
        verificationId: String,
        resendToken: PhoneAuthProvider.ForceResendingToken
    ) {
        this.username = username
        this.phone = phone
        this.passwordHash = passwordHash
        this.verificationId = verificationId
        this.resendToken = resendToken
    }

    fun getData(): RegistrationData? =
        if (username != null && phone != null && passwordHash != null && verificationId != null)
            RegistrationData(username!!, phone!!, passwordHash!!, verificationId!!, resendToken)
        else null

    fun clear() {
        username = null
        phone = null
        passwordHash = null
        verificationId = null
        resendToken = null
    }

    data class RegistrationData(
        val username: String,
        val phone: String,
        val passwordHash: String,
        val verificationId: String,
        val resendToken: PhoneAuthProvider.ForceResendingToken?
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    isLoading: Boolean = false,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onSignupClick: (String, String, String, String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick, enabled = !isLoading) {
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
            verticalArrangement = Arrangement.SpaceBetween   // ✅ cân bố cục, không overflow
        ) {

            // ---------------- TOP CONTENT ----------------
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Spacer(Modifier.height(8.dp))

                // ✅ Avatar (hiện tại là demo, bạn muốn mình gắn gallery/photo picker không?)
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(140.dp)
                        .clickable { /* TODO: mở chọn ảnh */ }
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    "Đăng ký",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(24.dp))

                // Username
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Tên người dùng") },
                    singleLine = true,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        focusedLabelColor = BluePrimary
                    )
                )

                Spacer(Modifier.height(16.dp))

                // Phone
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Số điện thoại") },
                    placeholder = { Text("0987654321") },
                    singleLine = true,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        focusedLabelColor = BluePrimary
                    )
                )

                Spacer(Modifier.height(16.dp))

                // Password
                PasswordInput(
                    label = "Mật khẩu",
                    value = password,
                    onValueChange = { password = it }
                )

                Spacer(Modifier.height(16.dp))

                // Confirm Password
                ConfirmPasswordInput(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it }
                )

                Spacer(Modifier.height(20.dp))

                // Signup Button
                Button(
                    onClick = { onSignupClick(username, phone, password, confirmPassword) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("ĐĂNG KÝ", color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            // ---------------- BOTTOM LOGIN ----------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .navigationBarsPadding(),  // ✅ chống bị cắt
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Đã có tài khoản?", color = GrayText, fontSize = 15.sp)
                Text(
                    " Đăng nhập ngay",
                    color = BluePrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable(enabled = !isLoading) { onLoginClick() }
                        .padding(start = 4.dp)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewSignupScreen() {
    ReLisAppTheme {
        SignupScreen(
            isLoading = false,
            onBackClick = {},
            onLoginClick = {},
            onSignupClick = { _, _, _, _ -> }
        )
    }
}