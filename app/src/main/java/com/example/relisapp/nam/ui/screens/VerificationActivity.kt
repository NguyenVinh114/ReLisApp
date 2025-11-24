package com.example.relisapp.nam.ui.screens

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
import androidx.compose.ui.text.style.TextAlign.Companion.Center
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
import com.example.relisapp.nam.viewmodel.AuthViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * VerificationActivity - Màn hình xác thực OTP
 *
 * ✅ MVVM Architecture chuẩn 2025
 * ✅ Xử lý đầy đủ lifecycle & memory leak
 * ✅ Loading state từ ViewModel
 * ✅ Cleanup Firebase callbacks
 * ✅ Error handling đầy đủ
 */
class VerificationActivity : ComponentActivity() {

    private lateinit var viewModel: AuthViewModel
    private var registrationData: RegistrationDataHolder.RegistrationData? = null

    // ✅ Giữ reference để cleanup callbacks và tránh memory leak
    private var phoneAuthCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inject ViewModel
        val factory = ViewModelProviderFactory.provideAuthViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        // Lấy dữ liệu từ SignupActivity
        registrationData = RegistrationDataHolder.getData()
        if (registrationData == null) {
            Toast.makeText(this, "Dữ liệu đăng ký đã bị xoá. Vui lòng nhập lại.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
            return
        }

        // Xử lý nút Back - xoá dữ liệu và quay lại Signup
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    RegistrationDataHolder.clear()
                    finish()
                }
            }
        )

        setContent {
            LearnTheme {
                // ✅ Observe loading state từ ViewModel (MVVM chuẩn)
                val isLoading by viewModel.isLoading.collectAsState()

                VerificationScreen(
                    phone = registrationData!!.phone,
                    isLoading = isLoading,
                    onBackClick = {
                        RegistrationDataHolder.clear()
                        finish()
                    },
                    onVerifyClick = { otp -> verifyOtpAndSaveUser(otp) },
                    onResendOtp = { resendOtp() }
                )
            }
        }
    }

    // ============================================================================================
    // VERIFY OTP VỚI CODE
    // ============================================================================================
    /**
     * Xác thực OTP người dùng nhập vào
     *
     * Flow:
     * 1. Validate OTP format
     * 2. Tạo credential từ verificationId + OTP
     * 3. SignIn với Firebase
     * 4. Lưu user vào database
     * 5. Sign out Firebase (quan trọng!)
     * 6. Navigate sang LoginActivity
     *
     * ✅ Reset loading ở MỌI nhánh (success/error)
     */
    private fun verifyOtpAndSaveUser(otp: String) {
        val data = registrationData ?: return

        // Validate OTP format - chỉ cho phép số
        if (!otp.all { it.isDigit() }) {
            Toast.makeText(this, "OTP chỉ chứa số", Toast.LENGTH_SHORT).show()
            return
        }

        // Bật loading state
        viewModel.setLoading(true)

        // Tạo credential từ verificationId + OTP
        val credential = PhoneAuthProvider.getCredential(
            data.verificationId,
            otp
        )

        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    // ========== SUCCESS FLOW ==========
                    lifecycleScope.launch {
                        try {
                            // Lưu user vào Room database
                            withContext(Dispatchers.IO) {
                                viewModel.registerUser(
                                    username = data.username,
                                    phone = data.phone,
                                    hashedPassword = data.passwordHash
                                )
                            }

                            // ✅ QUAN TRỌNG: Sign out Firebase sau khi đăng ký
                            // Nếu không → Firebase sẽ auto-login → sai flow
                            FirebaseAuth.getInstance().signOut()

                            // Clear dữ liệu tạm
                            RegistrationDataHolder.clear()

                            // ✅ Reset loading trước khi navigate
                            viewModel.setLoading(false)

                            Toast.makeText(
                                this@VerificationActivity,
                                "Đăng ký thành công!",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Navigate sang LoginActivity và clear back stack
                            val intent = Intent(this@VerificationActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()

                        } catch (e: Exception) {
                            // ✅ Reset loading khi có lỗi database
                            viewModel.setLoading(false)

                            Toast.makeText(
                                this@VerificationActivity,
                                "Lỗi khi lưu dữ liệu: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                } else {
                    // ========== ERROR FLOW ==========
                    // ✅ Reset loading khi OTP sai
                    viewModel.setLoading(false)

                    // Xử lý lỗi Firebase chi tiết
                    val errorMessage = when (val exception = task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> "Mã OTP không đúng"
                        is FirebaseTooManyRequestsException -> "Quá nhiều yêu cầu. Vui lòng thử lại sau"
                        is FirebaseNetworkException -> "Lỗi kết nối mạng"
                        else -> "Xác thực thất bại: ${exception?.message ?: "Lỗi không xác định"}"
                    }

                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

    // ============================================================================================
    // VERIFY OTP VỚI CREDENTIAL (CHO AUTO-VERIFICATION)
    // ============================================================================================
    /**
     * Xác thực tự động khi Firebase nhận diện OTP
     * Được gọi từ onVerificationCompleted khi smsCode = null
     *
     * ✅ Reset loading ở cả success và error flow
     */
    private fun verifyOtpAndSaveUserWithCredential(credential: PhoneAuthCredential) {
        val data = registrationData ?: return

        viewModel.setLoading(true)

        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->

                // ✅ Kiểm tra thất bại TRƯỚC để early return
                if (!task.isSuccessful) {
                    viewModel.setLoading(false)

                    val errorMessage = when (val exception = task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> "Credential không hợp lệ"
                        is FirebaseTooManyRequestsException -> "Quá nhiều yêu cầu"
                        is FirebaseNetworkException -> "Lỗi kết nối mạng"
                        else -> "Xác thực tự động thất bại: ${exception?.message}"
                    }

                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    return@addOnCompleteListener
                }

                // Success flow (giống verifyOtpAndSaveUser)
                lifecycleScope.launch {
                    try {
                        withContext(Dispatchers.IO) {
                            viewModel.registerUser(
                                username = data.username,
                                phone = data.phone,
                                hashedPassword = data.passwordHash
                            )
                        }

                        FirebaseAuth.getInstance().signOut()
                        RegistrationDataHolder.clear()
                        viewModel.setLoading(false)

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
                        viewModel.setLoading(false)
                        Toast.makeText(
                            this@VerificationActivity,
                            "Lỗi: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    // ============================================================================================
    // RESEND OTP
    // ============================================================================================
    /**
     * Gửi lại mã OTP
     *
     * ✅ Bật loading khi bắt đầu gửi
     * ✅ Tắt loading trong callbacks (onCodeSent/onVerificationFailed/onVerificationCompleted)
     * ✅ Lưu callbacks reference để cleanup
     */
    private fun resendOtp() {
        val data = registrationData ?: return
        val auth = FirebaseAuth.getInstance()

        // ⚠ TỐI ƯU 1: Bật loading khi bắt đầu gửi OTP
        viewModel.setLoading(true)

        // ✅ Tạo callbacks và lưu reference để cleanup sau
        phoneAuthCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            /**
             * Firebase tự động xác thực thành công
             * (Thường xảy ra khi: số đã từng verify, SIM hỗ trợ, hoặc instant verification)
             */
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // ⚠ TỐI ƯU 1: Tắt loading
                viewModel.setLoading(false)

                val code = credential.smsCode

                if (code != null) {
                    // Có mã OTP → verify bằng code
                    verifyOtpAndSaveUser(code)
                } else {
                    // Không có mã OTP → verify trực tiếp bằng credential
                    verifyOtpAndSaveUserWithCredential(credential)
                }

                Toast.makeText(
                    this@VerificationActivity,
                    "Xác thực tự động hoàn tất",
                    Toast.LENGTH_SHORT
                ).show()
            }

            /**
             * Gửi OTP thất bại
             */
            override fun onVerificationFailed(e: FirebaseException) {
                // ✅ Reset loading khi thất bại
                viewModel.setLoading(false)

                val errorMessage = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "Số điện thoại không hợp lệ"
                    is FirebaseTooManyRequestsException -> "Đã gửi quá nhiều OTP. Vui lòng thử lại sau"
                    is FirebaseNetworkException -> "Lỗi kết nối mạng"
                    else -> "Gửi OTP thất bại: ${e.message}"
                }

                Toast.makeText(
                    this@VerificationActivity,
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()
            }

            /**
             * OTP đã được gửi thành công
             */
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // ⚠ TỐI ƯU 1: Tắt loading sau khi gửi thành công
                viewModel.setLoading(false)

                // Cập nhật verificationId mới
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
        }

        // Build phone auth options
        val builder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(data.phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this@VerificationActivity)
            .setCallbacks(phoneAuthCallbacks!!)

        // Sử dụng resend token nếu có (gửi lại nhanh hơn)
        data.resendToken?.let { builder.setForceResendingToken(it) }

        PhoneAuthProvider.verifyPhoneNumber(builder.build())
    }

    // ============================================================================================
    // LIFECYCLE - CLEANUP
    // ============================================================================================
    /**
     * ✅ Cleanup callbacks khi Activity destroy
     * Quan trọng để tránh memory leak vì Firebase callbacks giữ reference đến Activity
     */
    override fun onDestroy() {
        super.onDestroy()
        phoneAuthCallbacks = null
    }
}

// ================================================================================================
// COMPOSABLE UI
// ================================================================================================
/**
 * Màn hình nhập OTP
 *
 * Features:
 * - 6 ô input riêng biệt cho từng số
 * - Auto focus sang ô tiếp theo
 * - Countdown timer 60s cho resend
 * - Disable tất cả controls khi đang loading
 * - Validation đầy đủ
 *
 * ✅ Countdown đúng logic với countdownKey
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(
    phone: String,
    isLoading: Boolean = false,
    onBackClick: () -> Unit,
    onVerifyClick: (String) -> Unit,
    onResendOtp: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var otpValues by remember { mutableStateOf(List(6) { "" }) }
    var resendTimer by remember { mutableIntStateOf(60) }
    var canResend by remember { mutableStateOf(false) }
    var countdownKey by remember { mutableIntStateOf(0) } // Key để restart countdown

    // ✅ Countdown logic - chỉ chạy lại khi countdownKey thay đổi
    LaunchedEffect(countdownKey) {
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
                    IconButton(onClick = onBackClick, enabled = !isLoading) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (isLoading) GrayText.copy(alpha = 0.5f) else GrayText
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

            Text(
                "Nhập mã OTP",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "OTP đã gửi đến số:",
                fontSize = 14.sp,
                color = GrayText
            )

            Text(
                phone,
                fontSize = 16.sp,
                color = BluePrimary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(32.dp))

            // ========== 6 OTP INPUT BOXES ==========
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                otpValues.forEachIndexed { index, value ->
                    OutlinedTextField(
                        value = value,
                        onValueChange = { new ->
                            // Chỉ cho phép 1 số
                            if (new.length <= 1 && new.all { it.isDigit() }) {
                                otpValues = otpValues.toMutableList().also { it[index] = new }

                                // Auto focus
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
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = Center
                        ),
                        enabled = !isLoading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            disabledBorderColor = GrayText.copy(alpha = 0.3f)
                        )
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // ========== VERIFY BUTTON ==========
            Button(
                onClick = {
                    val otpCode = otpValues.joinToString("")

                    // Validation đầy đủ
                    when {
                        otpCode.length != 6 -> {
                            Toast.makeText(context, "Nhập đủ 6 số OTP", Toast.LENGTH_SHORT).show()
                        }
                        !otpCode.all { it.isDigit() } -> {
                            Toast.makeText(context, "OTP chỉ chứa số", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            onVerifyClick(otpCode)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
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
                    Text(
                        "XÁC THỰC",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ========== RESEND OTP ==========
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Không nhận được mã?", color = GrayText, fontSize = 14.sp)
                Spacer(Modifier.width(4.dp))

                TextButton(
                    onClick = {
                        if (canResend && !isLoading) {
                            // ✅ Restart countdown bằng cách tăng key
                            countdownKey++
                            otpValues = List(6) { "" }
                            onResendOtp()
                        }
                    },
                    enabled = canResend && !isLoading
                ) {
                    Text(
                        text = if (canResend && !isLoading) "Gửi lại" else "Gửi lại (${resendTimer}s)",
                        color = if (canResend && !isLoading) BluePrimary else GrayText,
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

// ================================================================================================
// PREVIEW
// ================================================================================================
@Preview(showBackground = true)
@Composable
fun PreviewVerificationScreen() {
    LearnTheme {
        VerificationScreen(
            phone = "+84901234567",
            onBackClick = {},
            onVerifyClick = {},
            onResendOtp = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun PreviewVerificationScreenLoading() {
    LearnTheme {
        VerificationScreen(
            phone = "+84901234567",
            isLoading = true,
            onBackClick = {},
            onVerifyClick = {},
            onResendOtp = {}
        )
    }
}