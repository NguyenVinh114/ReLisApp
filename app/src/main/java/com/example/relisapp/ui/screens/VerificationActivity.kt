package com.example.relisapp.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.relisapp.R
import com.example.relisapp.di.ViewModelProviderFactory
import com.example.relisapp.ui.theme.BluePrimary
import com.example.relisapp.ui.theme.GrayText
import com.example.relisapp.ui.theme.ReLisAppTheme
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.data.repository.UserRepository
import com.example.relisapp.data.local.AppDatabase
import com.example.relisapp.viewmodel.AuthViewModel
import androidx.activity.OnBackPressedCallback



class VerificationActivity : ComponentActivity() {

    private var registrationData: RegistrationDataHolder.RegistrationData? = null
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        // 🔥 Lấy ViewModel từ DI mới
        val factory = ViewModelProviderFactory.provideAuthViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        registrationData = RegistrationDataHolder.getData()
        if (registrationData == null) {
            Toast.makeText(this, "Dữ liệu đăng ký đã bị xoá. Vui lòng nhập lại.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
            return
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    RegistrationDataHolder.clear()
                    finish()  // trở về SignupActivity
                }
            }
        )

        setContent {
            ReLisAppTheme {
                VerificationScreen(
                    phone = registrationData!!.phone,
                    onBackClick = { RegistrationDataHolder.clear()
                        finish() },
                    onVerifyClick = { otp -> verifyOtpAndSaveUser(otp) },
                    onResendOtp = { resendOtp() }
                )
            }
        }
    }




    private fun verifyOtpAndSaveUser(otp: String) {
        val data = registrationData ?: return
        val credential = PhoneAuthProvider.getCredential(data.verificationId, otp)

        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                // 🌟 Lưu user vào Room thông qua ViewModel
                lifecycleScope.launch {

                    try {
                        withContext(Dispatchers.IO) {
                            viewModel.registerUser(
                                username = data.username,
                                phone = data.phone,
                                passwordHash = data.passwordHash
                            )
                        }

                        RegistrationDataHolder.clear()

                        Toast.makeText(
                            this@VerificationActivity,
                            "Đăng ký thành công!",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@VerificationActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()

                    } catch (e: Exception) {
                        Toast.makeText(
                            this@VerificationActivity,
                            "Lỗi khi lưu dữ liệu: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } else {
                Toast.makeText(this, "Mã OTP không hợp lệ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatToInternationalPhone(phone: String): String {
        val clean = phone.removePrefix("+84").removePrefix("0")
        return "+84$clean"
    }


    private fun resendOtp() {
        val data = registrationData ?: return

        val auth = FirebaseAuth.getInstance()

        lifecycleScope.launch {

            // ⭐ 1. KIỂM TRA SỐ ĐIỆN THOẠI ĐÃ TỒN TẠI CHƯA
            val db = AppDatabase.getDatabase(this@VerificationActivity)
            val repo = UserRepository(db.userDao())
            val normalizedPhone = formatToInternationalPhone(data.phone)
            val exists = repo.isPhoneExists(normalizedPhone)


            if (exists) {
                Toast.makeText(
                    this@VerificationActivity,
                    "Số điện thoại đã tồn tại trong hệ thống. Không thể gửi lại OTP.",
                    Toast.LENGTH_LONG
                ).show()
                return@launch
            }

            // ⭐ 2. GỬI LẠI OTP NẾU SỐ CHƯA TỒN TẠI
            val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(formatToInternationalPhone(data.phone))
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this@VerificationActivity)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    override fun onVerificationCompleted(credential: com.google.firebase.auth.PhoneAuthCredential) {
                        Toast.makeText(
                            this@VerificationActivity,
                            "Xác thực tự động hoàn tất",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        Toast.makeText(
                            this@VerificationActivity,
                            "Gửi lại OTP thất bại: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }


                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        registrationData = data.copy(
                            verificationId = verificationId,
                            resendToken = token
                        )

                        RegistrationDataHolder.setData(
                            username = data.username,
                            phone = data.phone,
                            passwordHash = data.passwordHash,
                            verificationId = verificationId,
                            resendToken = token
                        )

                        Toast.makeText(
                            this@VerificationActivity,
                            "Đã gửi lại mã OTP",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

            // ⭐ 3. GIỮ LẠI FORCE RESEND TOKEN (nếu có)
            data.resendToken?.let {
                optionsBuilder.setForceResendingToken(it)
            }

            // ⭐ 4. THỰC SỰ GỬI OTP
            PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(
    phone: String,
    onBackClick: () -> Unit,
    onVerifyClick: (String) -> Unit,
    onResendOtp: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var otpValues by remember { mutableStateOf(List(6) { "" }) }
    var isVerifying by remember { mutableStateOf(false) }
    var resendTimer by remember { mutableStateOf(60) }
    var canResend by remember { mutableStateOf(false) }

    // Countdown timer
    LaunchedEffect(canResend) {
        if (!canResend && resendTimer > 0) {
            while (resendTimer > 0) {
                delay(1000)
                resendTimer--
            }
            canResend = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Xác thực OTP", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, enabled = !isVerifying) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GrayText)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(80.dp)
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Nhập mã OTP",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Mã OTP đã được gửi tới",
                fontSize = 14.sp,
                color = GrayText,
                textAlign = TextAlign.Center
            )

            Text(
                text = phone,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = BluePrimary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            // OTP Input Fields
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                otpValues.forEachIndexed { index, value ->
                    OutlinedTextField(
                        value = value,
                        onValueChange = { new ->
                            if (new.length <= 1 && new.all { it.isDigit() }) {
                                otpValues = otpValues.toMutableList().also { it[index] = new }
                                when {
                                    new.isNotEmpty() && index < 5 ->
                                        focusManager.moveFocus(FocusDirection.Next)
                                    new.isEmpty() && index > 0 ->
                                        focusManager.moveFocus(FocusDirection.Previous)
                                }
                            }
                        },
                        singleLine = true,
                        enabled = !isVerifying,
                        modifier = Modifier
                            .width(48.dp)
                            .height(56.dp),
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Verify Button
            Button(
                onClick = {
                    val otpCode = otpValues.joinToString("")
                    if (otpCode.length == 6) {
                        isVerifying = true
                        onVerifyClick(otpCode)
                    } else {
                        Toast.makeText(
                            context,
                            "Vui lòng nhập đủ 6 số OTP",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isVerifying && otpValues.joinToString("").length == 6,
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                if (isVerifying) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "XÁC THỰC",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Resend OTP Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Không nhận được mã?", color = GrayText, fontSize = 14.sp)
                Spacer(Modifier.width(4.dp))
                TextButton(
                    onClick = {
                        if (canResend) {
                            canResend = false
                            resendTimer = 60
                            otpValues = List(6) { "" }
                            onResendOtp()
                        }
                    },
                    enabled = canResend && !isVerifying
                ) {
                    Text(
                        text = if (canResend) "Gửi lại" else "Gửi lại (${resendTimer}s)",
                        color = if (canResend) BluePrimary else GrayText,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Mã OTP có hiệu lực trong 10 phút",
                fontSize = 12.sp,
                color = GrayText,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VerificationScreenPreview() {
    ReLisAppTheme {
        VerificationScreen(
            phone = "+84987654321",
            onBackClick = {},
            onVerifyClick = {},
            onResendOtp = {}
        )
    }
}