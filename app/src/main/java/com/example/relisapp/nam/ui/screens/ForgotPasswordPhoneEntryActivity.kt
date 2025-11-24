package com.example.relisapp.nam.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.relisapp.R
import com.example.relisapp.nam.di.ViewModelProviderFactory
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
import java.util.concurrent.TimeUnit

class ForgotPasswordPhoneEntryActivity : ComponentActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var auth: FirebaseAuth
    private var isLoading by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = ViewModelProviderFactory.provideAuthViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
        auth = FirebaseAuth.getInstance()

        setContent {
            LearnTheme {
                ForgotPasswordPhoneEntryScreen(
                    isLoading = isLoading,
                    onBackClick = { finish() },
                    onSendOtpClick = { phone -> handleSendOtp(phone) }
                )
            }
        }
    }

    private fun handleSendOtp(phone: String) {
        // Validate phone number
        val error = validatePhone(phone)
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            return
        }

        isLoading = true

        // Check if phone exists in database
        lifecycleScope.launch {
            try {
                val user = viewModel.getUserByPhone(formatPhoneNumber(phone))

                if (user == null) {
                    isLoading = false
                    Toast.makeText(
                        this@ForgotPasswordPhoneEntryActivity,
                        "Số điện thoại chưa được đăng ký",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                // Send OTP via Firebase
                sendFirebaseOtp(formatPhoneNumber(phone), user.userId)


            } catch (e: Exception) {
                isLoading = false
                Toast.makeText(
                    this@ForgotPasswordPhoneEntryActivity,
                    "Lỗi: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun sendFirebaseOtp(phone: String, userId: Int) {
        val phoneNumber = formatPhoneNumber(phone)

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    isLoading = false
                    Toast.makeText(
                        this@ForgotPasswordPhoneEntryActivity,
                        "Xác thực tự động hoàn tất",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Auto-verification successful - proceed to password reset
                    navigateToChangePassword(userId)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    isLoading = false
                    Toast.makeText(
                        this@ForgotPasswordPhoneEntryActivity,
                        "Gửi OTP thất bại: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    isLoading = false

                    // Lưu thông tin để xác minh OTP
                    ForgotPasswordDataHolder.setData(
                        userId = userId,
                        phone = phoneNumber,
                        verificationId = verificationId,
                        resendToken = token
                    )

                    // ❗❗❗ FIX QUAN TRỌNG — Mở đúng màn OTP Verification
                    startActivity(
                        Intent(
                            this@ForgotPasswordPhoneEntryActivity,
                            ForgotPasswordOtpVerificationActivity::class.java
                        )
                    )
                }

            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun navigateToChangePassword(userId: Int) {
        val intent = Intent(this, ChangeForgotPasswordActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
        finish()
    }

    private fun validatePhone(phone: String): String? {
        return when {
            phone.isBlank() -> "Vui lòng nhập số điện thoại"
            !isValidVietnamesePhone(phone) -> "Số điện thoại không hợp lệ"
            else -> null
        }
    }

    private fun isValidVietnamesePhone(phone: String): Boolean {
        val clean = phone.removePrefix("+84").removePrefix("0")
        return clean.matches(Regex("^[0-9]{9,10}$"))
    }

    private fun formatPhoneNumber(phone: String): String {
        return if (phone.startsWith("+")) phone else "+84${phone.removePrefix("0")}"
    }
}

// ============================
// DATA HOLDER
// ============================
object ForgotPasswordDataHolder {
    private var userId: Int? = null
    private var phone: String? = null
    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    fun setData(
        userId: Int,
        phone: String,
        verificationId: String,
        resendToken: PhoneAuthProvider.ForceResendingToken
    ) {
        this.userId = userId
        this.phone = phone
        this.verificationId = verificationId
        this.resendToken = resendToken
    }

    fun getData(): ForgotPasswordData? {
        return if (userId != null && phone != null && verificationId != null) {
            ForgotPasswordData(
                userId = userId!!,
                phone = phone!!,
                verificationId = verificationId!!,
                resendToken = resendToken
            )
        } else null
    }

    fun clear() {
        userId = null
        phone = null
        verificationId = null
        resendToken = null
    }

    data class ForgotPasswordData(
        val userId: Int,
        val phone: String,
        val verificationId: String,
        val resendToken: PhoneAuthProvider.ForceResendingToken?
    )
}

// ============================
// COMPOSABLE UI
// ============================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordPhoneEntryScreen(
    isLoading: Boolean = false,
    onBackClick: () -> Unit,
    onSendOtpClick: (String) -> Unit
) {
    var phone by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quên mật khẩu") },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Đặt lại mật khẩu",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Nhập số điện thoại đã đăng ký để nhận mã OTP",
                fontSize = 14.sp,
                color = GrayText,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Số điện thoại") },
                placeholder = { Text("0987654321") },
                singleLine = true,
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BluePrimary,
                    focusedLabelColor = BluePrimary
                )
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { onSendOtpClick(phone) },
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
                        text = "GỬI MÃ OTP",
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Mã OTP sẽ được gửi đến số điện thoại của bạn",
                fontSize = 12.sp,
                color = GrayText
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewForgotPasswordPhoneEntryScreen() {
    LearnTheme {
        ForgotPasswordPhoneEntryScreen(
            isLoading = false,
            onBackClick = {},
            onSendOtpClick = {}
        )
    }
}