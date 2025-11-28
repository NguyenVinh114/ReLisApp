package com.example.relisapp.nam.ui.screens.auth

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.relisapp.R
import com.example.relisapp.nam.di.ViewModelProviderFactory
import com.example.relisapp.nam.ui.components.inputs.ConfirmPasswordInput
import com.example.relisapp.nam.ui.components.inputs.PasswordInput
import com.example.relisapp.nam.ui.theme.BluePrimary
import com.example.relisapp.nam.ui.theme.GrayText
import com.example.relisapp.nam.ui.theme.LearnTheme
import com.example.relisapp.nam.ui.theme.White
import com.example.relisapp.nam.viewmodel.AuthViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

class SignupActivity : ComponentActivity()  {

    private lateinit var viewModel: AuthViewModel
    private lateinit var auth: FirebaseAuth

    private var isLoading by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = ViewModelProviderFactory.provideAuthViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        // Firebase Auth
        auth = FirebaseAuth.getInstance()

        setContent {
            LearnTheme {
                SignupScreen(
                    isLoading = isLoading,
                    onBackClick = {
                        RegistrationDataHolder.clear()
                        finish()
                    },
                    onLoginClick = {
                        RegistrationDataHolder.clear()
                        finish()
                    },
                    onSignupClick = { username, phone, password, confirmPassword ->
                        handleSignup(username, phone, password, confirmPassword)
                    }
                )
            }
        }
    }
    private fun handleSignup(
        username: String,
        phone: String,
        password: String,
        confirmPassword: String
    ) {
        val error = validateInputs(username, phone, password, confirmPassword)
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            return
        }

        // Format phone về dạng +84... để dùng thống nhất trong DB
        val formattedPhone = formatPhoneNumber(phone)

        isLoading = true

        lifecycleScope.launch {
            try {
                // Kiểm tra trùng username / phone
                val existingUser = viewModel.checkUserExists(username, formattedPhone)
                if (existingUser != null) {
                    isLoading = false
                    Toast.makeText(
                        this@SignupActivity,
                        "Tên người dùng hoặc số điện thoại đã tồn tại",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Gửi OTP qua Firebase
                    sendOtpWithFirebase(username, phone, password)
                }
            } catch (e: Exception) {
                isLoading = false
                Toast.makeText(
                    this@SignupActivity,
                    "Lỗi: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun validateInputs(
        username: String,
        phone: String,
        password: String,
        confirmPassword: String
    ): String? {
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

    private fun formatPhoneNumber(phone: String): String {
        val cleaned = phone.trim()

        return when {
            cleaned.startsWith("+84") -> cleaned
            cleaned.startsWith("84") -> "+$cleaned"
            cleaned.startsWith("0") -> "+84${cleaned.substring(1)}" // Bỏ số 0 đầu
            else -> "+84$cleaned"
        }
    }

    private fun sendOtpWithFirebase(
        username: String,
        phone: String,
        password: String
    ) {
        val phoneNumber = formatPhoneNumber(phone)
        val passwordHash = hashPassword(password)

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                // Trường hợp xác thực tự động (instant verification)
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    isLoading = false
                    Toast.makeText(
                        this@SignupActivity,
                        "Xác thực tự động hoàn tất",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Tự động sign-in với credential rồi lưu user vào Room
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                lifecycleScope.launch {
                                    try {
                                        viewModel.registerUser(
                                            username = username,
                                            phone = phoneNumber,
                                            password = passwordHash
                                        )
                                        RegistrationDataHolder.clear()

                                        Toast.makeText(
                                            this@SignupActivity,
                                            "Đăng ký thành công!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        // Điều hướng về màn Login, xóa back stack
                                        val intent = Intent(
                                            this@SignupActivity,
                                            LoginActivity::class.java
                                        ).apply {
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        }
                                        startActivity(intent)
                                        finish()

                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            this@SignupActivity,
                                            "Lỗi khi lưu dữ liệu: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    this@SignupActivity,
                                    "Xác thực tự động thất bại",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    isLoading = false
                    Toast.makeText(
                        this@SignupActivity,
                        "Gửi OTP thất bại: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    isLoading = false

                    // Lưu data để dùng ở VerificationActivity (manual OTP)
                    RegistrationDataHolder.setData(
                        username = username,
                        phone = phoneNumber,
                        passwordHash = passwordHash,
                        verificationId = verificationId,
                        resendToken = token
                    )

                    // Điều hướng đến màn hình nhập OTP
                    val intent = Intent(
                        this@SignupActivity,
                        VerificationActivity::class.java
                    ).apply {
                        // Xóa back stack để không quay lại Signup giữa chừng
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // Hash password SHA-256
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray())
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
        if (username != null && phone != null && passwordHash != null && verificationId != null) {
            RegistrationData(
                username = username!!,
                phone = phone!!,
                passwordHash = passwordHash!!,
                verificationId = verificationId!!,
                resendToken = resendToken
            )
        } else null

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // ---------- TOP CONTENT ----------
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Spacer(Modifier.height(8.dp))

                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(140.dp)
                        .clickable(enabled = !isLoading) {
                            // TODO: mở chọn ảnh nếu cần
                        }
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Đăng ký",
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

                // Signup button
                Button(
                    onClick = {
                        onSignupClick(
                            username,
                            phone,
                            password,
                            confirmPassword
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BluePrimary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "ĐĂNG KÝ",
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            // ---------- BOTTOM LOGIN ----------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .navigationBarsPadding(),
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
