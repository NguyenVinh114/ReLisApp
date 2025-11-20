package com.example.relisapp.ui.screens

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.relisapp.R
import com.example.relisapp.data.local.SessionManager
import com.example.relisapp.di.ViewModelProviderFactory
import com.example.relisapp.ui.theme.ReLisAppTheme
import com.example.relisapp.viewmodel.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InfoProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val session = SessionManager(this)
        val userId = session.getUserId()

        val factory = ViewModelProviderFactory.provideAuthViewModelFactory(this)
        val viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        setContent {
            ReLisAppTheme {

                var username by remember { mutableStateOf("") }
                var fullName by remember { mutableStateOf("") }
                var email by remember { mutableStateOf("") }
                var phone by remember { mutableStateOf("") }
                var avatarBytes by remember { mutableStateOf<ByteArray?>(null) }

                // Load thông tin user
                LaunchedEffect(userId) {
                    val user = viewModel.getUserById(userId)
                    if (user != null) {
                        username = user.username
                        fullName = user.fullName ?: ""
                        email = user.email ?: ""
                        phone = user.phoneNumber ?: ""
                        avatarBytes = user.avatar
                    }
                }

                // Chọn ảnh avatar
                val pickImageLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri ->
                    if (uri != null) {
                        val inputStream = contentResolver.openInputStream(uri)
                        avatarBytes = inputStream?.readBytes()
                    }
                }

                InfoProfileScreen(
                    username = username,
                    fullName = fullName,
                    email = email,
                    phone = phone,
                    avatarBytes = avatarBytes,
                    onBackClick = { finish() },

                    // ✔ CHO SỬA USERNAME
                    onUsernameChange = { username = it },

                    // ✔ CHO SỬA FULL NAME
                    onFullNameChange = { fullName = it },

                    // Lưu dữ liệu
                    onSaveClick = {
                        lifecycleScope.launch(Dispatchers.IO) {

                            viewModel.updateUsername(username)
                            viewModel.updateFullName(fullName)
                            viewModel.updateAvatar(avatarBytes)

                            launch(Dispatchers.Main) {

                                // ⭐ Thông báo thành công
                                Toast.makeText(this@InfoProfileActivity, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()

                                val resultIntent = Intent().apply {
                                    putExtra("updated", true) // Báo hiệu đã có cập nhật
                                }
                                setResult(RESULT_OK, resultIntent) // Thiết lập kết quả thành công
                                finish() // Kết thúc InfoProfileActivity, quay về ProfileActivity
                            }
                        }
                    },



                            onPickAvatar = {
                        pickImageLauncher.launch("image/*")
                    },

                    onDeleteAvatar = {
                        avatarBytes = null
                        lifecycleScope.launch(Dispatchers.IO) {
                            viewModel.updateAvatar(null)
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoProfileScreen(
    username: String,
    fullName: String,
    email: String,
    phone: String,
    avatarBytes: ByteArray?,
    onBackClick: () -> Unit,
    onUsernameChange: (String) -> Unit,
    onFullNameChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onPickAvatar: () -> Unit,
    onDeleteAvatar: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông tin tài khoản") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Lưu thay đổi", color = Color.White, fontSize = 16.sp)
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Avatar
            val bitmap = avatarBytes?.let {
                BitmapFactory.decodeByteArray(it, 0, it.size)
            }

            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .clickable { onPickAvatar() },
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.outline_article_person_24),
                    contentDescription = "Default Avatar",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray.copy(alpha = 0.3f))
                        .clickable { onPickAvatar() },
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Thay đổi ảnh đại diện",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onPickAvatar() }
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Xóa ảnh",
                color = Color.Red,
                modifier = Modifier.clickable { onDeleteAvatar() }
            )

            Spacer(Modifier.height(24.dp))

            // ✔ EDIT USERNAME
            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                label = { Text("Tên đăng nhập") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // ✔ EDIT FULL NAME
            OutlinedTextField(
                value = fullName,
                onValueChange = onFullNameChange,
                label = { Text("Họ và tên") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {},
                enabled = false,
                label = { Text("Email (không thể thay đổi)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = {},
                enabled = false,
                label = { Text("Số điện thoại (không thể thay đổi)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

