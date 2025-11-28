package com.example.relisapp.nam.ui.screens.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.relisapp.R
import com.example.relisapp.nam.ui.theme.BluePrimary
import com.example.relisapp.nam.ui.theme.GrayText
import com.example.relisapp.nam.ui.theme.LearnTheme
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

class ForgotPasswordOtpVerificationActivity : ComponentActivity() {

    private var forgotPasswordData: ForgotPasswordDataHolder.ForgotPasswordData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        forgotPasswordData = ForgotPasswordDataHolder.getData()
        if (forgotPasswordData == null) {
            Toast.makeText(
                this,
                "Dữ liệu xác thực không hợp lệ. Vui lòng thử lại.",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        // Back button handler
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    ForgotPasswordDataHolder.clear()
                    finish()
                }
            }
        )

        setContent {
            LearnTheme {
                ForgotPasswordOtpVerificationScreen(
                    phone = forgotPasswordData!!.phone,
                    onBackClick = {
                        ForgotPasswordDataHolder.clear()
                        finish()
                    },
                    onVerifyClick = { otp -> verifyOtp(otp) },
                    onResendOtp = { resendOtp() }
                )
            }
        }
    }

    // ========================================================
    // VERIFY OTP
    // ========================================================
    private fun verifyOtp(otp: String) {
        val data = forgotPasswordData ?: return

        val credential = PhoneAuthProvider.getCredential(
            data.verificationId,
            otp
        )

        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    // Clear temporary data
                    ForgotPasswordDataHolder.clear()

                    // Navigate to change password
                    val intent = Intent(
                        this,
                        ChangeForgotPasswordActivity::class.java
                    )
                    intent.putExtra("userId", data.userId)
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(
                        this,
                        "Mã OTP không hợp lệ",
                        Toast.LENGTH_SHORT
                    ).show()

                    // ❗ UI unlock
                    ForgotPasswordOtpUiState.isVerifying.value = false
                }
            }
    }

    // ========================================================
    // RESEND OTP
    // ========================================================
    private fun resendOtp() {
        val data = forgotPasswordData ?: return
        val auth = FirebaseAuth.getInstance()

        val builder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(data.phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Toast.makeText(
                        this@ForgotPasswordOtpVerificationActivity,
                        "Xác thực tự động hoàn tất",
                        Toast.LENGTH_SHORT
                    ).show()

                    ForgotPasswordDataHolder.clear()

                    val intent = Intent(
                        this@ForgotPasswordOtpVerificationActivity,
                        ChangeForgotPasswordActivity::class.java
                    )
                    intent.putExtra("userId", data.userId)
                    startActivity(intent)
                    finish()
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(
                        this@ForgotPasswordOtpVerificationActivity,
                        "Gửi OTP thất bại: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    forgotPasswordData = data.copy(
                        verificationId = verificationId,
                        resendToken = token
                    )

                    ForgotPasswordDataHolder.setData(
                        userId = data.userId,
                        phone = data.phone,
                        verificationId = verificationId,
                        resendToken = token
                    )

                    Toast.makeText(
                        this@ForgotPasswordOtpVerificationActivity,
                        "Đã gửi lại mã OTP",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        data.resendToken?.let { builder.setForceResendingToken(it) }

        PhoneAuthProvider.verifyPhoneNumber(builder.build())
    }
}

/* ======================================================================
                              UI STATE HOLDER
====================================================================== */
object ForgotPasswordOtpUiState {
    val isVerifying = mutableStateOf(false)
}

/* ======================================================================
                              COMPOSE UI
====================================================================== */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordOtpVerificationScreen(
    phone: String,
    onBackClick: () -> Unit,
    onVerifyClick: (String) -> Unit,
    onResendOtp: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var otpValues by remember { mutableStateOf(List(6) { "" }) }
    val isVerifying = ForgotPasswordOtpUiState.isVerifying

    var resendTimer by remember { mutableIntStateOf(60) }
    var canResend by remember { mutableStateOf(false) }
    var resendTrigger by remember { mutableStateOf(false) }

    // Countdown
    LaunchedEffect(resendTrigger) {
        resendTimer = 60
        canResend = false
        while (resendTimer > 0) {
            delay(1000)
            resendTimer--
        }
        canResend = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Xác thực OTP", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, enabled = !isVerifying.value) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GrayText
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
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

            Text("Nhập mã OTP",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(6.dp))

            Text("OTP đã gửi đến số:",
                fontSize = 14.sp, color = GrayText)

            Text(phone,
                fontSize = 16.sp,
                color = BluePrimary,
                fontWeight = FontWeight.SemiBold)

            Spacer(Modifier.height(32.dp))

            // OTP INPUT
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
                                    new.isNotEmpty() && index < 5 -> focusManager.moveFocus(FocusDirection.Next)
                                    new.isEmpty() && index > 0 -> focusManager.moveFocus(FocusDirection.Previous)
                                }
                            }
                        },
                        modifier = Modifier
                            .width(48.dp)
                            .height(56.dp),
                        singleLine = true,
                        enabled = !isVerifying.value,
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary
                        )
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // VERIFY BUTTON
            Button(
                onClick = {
                    val otpCode = otpValues.joinToString("")
                    if (otpCode.length == 6) {
                        isVerifying.value = true
                        onVerifyClick(otpCode)
                    } else {
                        Toast.makeText(context, "Nhập đủ 6 số OTP", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isVerifying.value,
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                if (isVerifying.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("XÁC THỰC", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(24.dp))

            // RESEND OTP
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Không nhận được mã?", color = GrayText, fontSize = 14.sp)
                Spacer(Modifier.width(4.dp))

                TextButton(
                    onClick = {
                        if (canResend) {
                            resendTrigger = !resendTrigger
                            otpValues = List(6) { "" }   // reset UI
                            onResendOtp()
                        }
                    },
                    enabled = canResend
                ) {
                    Text(
                        text = if (canResend) "Gửi lại" else "Gửi lại (${resendTimer}s)",
                        color = if (canResend) BluePrimary else GrayText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Mã OTP có hiệu lực trong 10 phút",
                fontSize = 12.sp,
                color = GrayText
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewForgotPasswordOtpVerificationScreen() {
    LearnTheme {
        ForgotPasswordOtpVerificationScreen(
            phone = "+84901234567",
            onBackClick = {},
            onVerifyClick = {},
            onResendOtp = {}
        )
    }
}
