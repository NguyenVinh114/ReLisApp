package com.example.relisapp.nam.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.R
import com.example.relisapp.nam.data.local.SessionManager
import com.example.relisapp.nam.di.ViewModelProviderFactory
import com.example.relisapp.nam.database.entity.User
import com.example.relisapp.nam.ui.theme.BluePrimary
import com.example.relisapp.nam.ui.theme.LearnTheme
import com.example.relisapp.nam.database.Converters.BitmapConverter
import com.example.relisapp.nam.viewmodel.AuthViewModel
import com.example.relisapp.nam.viewmodel.ProfileUpdateState

class InfoProfileActivity : ComponentActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        session = SessionManager(this)
        val userId = session.getUserId()

        if (userId == -1) {
            Toast.makeText(this, "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val factory = ViewModelProviderFactory.provideAuthViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        viewModel.loadUser(userId)

        setContent {
            LearnTheme {
                val user by viewModel.currentUser.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()
                val updateState by viewModel.profileUpdateState.collectAsState()
                val context = LocalContext.current

                var username by remember { mutableStateOf("") }
                var fullName by remember { mutableStateOf("") }
                var avatarBytes by remember { mutableStateOf<ByteArray?>(null) }
                var age by remember { mutableStateOf("") } // ⭐ Editable Age

                LaunchedEffect(user) {
                    user?.let {
                        username = it.username
                        fullName = it.fullName ?: ""
                        avatarBytes = it.avatar
                        age = it.age?.toString() ?: ""
                    }
                }

                LaunchedEffect(updateState) {
                    when (updateState) {
                        is ProfileUpdateState.Success -> {
                            Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                            viewModel.resetUpdateState()
                            finish()
                        }
                        is ProfileUpdateState.Error -> {
                            Toast.makeText(
                                context,
                                (updateState as ProfileUpdateState.Error).message,
                                Toast.LENGTH_LONG
                            ).show()
                            viewModel.resetUpdateState()
                        }
                        else -> {}
                    }
                }

                val pickImageLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri: Uri? ->
                    uri?.let {
                        try {
                            val stream = contentResolver.openInputStream(it)
                            val bitmap = BitmapFactory.decodeStream(stream)
                            stream?.close()

                            avatarBytes = BitmapConverter.prepareAvatarForDatabase(bitmap)

                        } catch (e: Exception) {
                            Toast.makeText(context, "Lỗi khi đọc ảnh: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                InfoProfileScreen(
                    user = user,
                    username = username,
                    fullName = fullName,
                    age = age,                   // ⭐ Editable Age
                    avatarBytes = avatarBytes,
                    isLoading = isLoading,
                    onBackClick = { finish() },
                    onUsernameChange = { username = it },
                    onFullNameChange = { fullName = it },
                    onAgeChange = { age = it }, // ⭐ Editable Age
                    onPickAvatar = { pickImageLauncher.launch("image/*") },
                    onDeleteAvatar = { avatarBytes = null },
                    onSaveClick = {
                        when {
                            username.isBlank() -> showToast(context, "Tên đăng nhập không được để trống")
                            fullName.isBlank() -> showToast(context, "Họ và tên không được để trống")
                            age.isNotBlank() && age.toIntOrNull() == null -> showToast(context, "Tuổi phải là số hợp lệ")
                            else -> {
                                viewModel.updateProfile(
                                    userId = userId,
                                    username = username.trim(),
                                    fullName = fullName.trim(),
                                    avatarBytes = avatarBytes,
                                    age = age.toIntOrNull() // ⭐ Save Age
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

fun showToast(context: android.content.Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}

// =========================== COMPOSABLE UI ===========================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoProfileScreen(
    user: User?,
    username: String,
    fullName: String,
    age: String, // Đảm bảo tham số này nằm ở đây
    avatarBytes: ByteArray?,
    isLoading: Boolean = false,
    onBackClick: () -> Unit,
    onUsernameChange: (String) -> Unit,
    onFullNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onPickAvatar: () -> Unit,
    onDeleteAvatar: () -> Unit // Tham số này sẽ được dùng bên dưới
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông tin tài khoản") },
                navigationIcon = {
                    IconButton(onClick = onBackClick, enabled = !isLoading) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = onSaveClick,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Lưu thay đổi", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ================= AVATAR =================
            val bitmap = avatarBytes?.let { BitmapConverter.byteArrayToBitmap(it) }

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(enabled = !isLoading) { onPickAvatar() },
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.outline_article_person_24),
                        contentDescription = "Default Avatar",
                        modifier = Modifier.size(60.dp),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                            MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            // --- KHẮC PHỤC CẢNH BÁO: Thêm nút xóa ảnh nếu đã có ảnh ---
            if (avatarBytes != null) {
                TextButton(onClick = onDeleteAvatar, enabled = !isLoading) {
                    Text("Xóa ảnh", color = MaterialTheme.colorScheme.error)
                }
            } else {
                Spacer(Modifier.height(16.dp))
            }
            // -----------------------------------------------------------

            // ================= Username =================
            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                label = { Text("Tên đăng nhập") },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // ================= Full Name =================
            OutlinedTextField(
                value = fullName,
                onValueChange = onFullNameChange,
                label = { Text("Họ và tên") },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // ================= AGE (Editable) =================
            OutlinedTextField(
                value = age,
                onValueChange = onAgeChange,
                label = { Text("Tuổi") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // ================= LEVEL (Readonly) =================
            OutlinedTextField(
                value = user?.level ?: "-",
                onValueChange = {},
                enabled = false,
                label = { Text("Trình độ (không thể thay đổi)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // ================= Account Info =================
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {

                    Text("Thông tin tài khoản", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))

                    InfoRow("Vai trò", user?.role ?: "-")
                    InfoRow("Trạng thái", user?.accountStatus ?: "-")
                    InfoRow("Xác thực", if (user?.isVerified == true) "Đã xác thực" else "Chưa xác thực")
                    InfoRow("Ngày tạo", user?.createdAt ?: "-")
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Medium)
    }
}
