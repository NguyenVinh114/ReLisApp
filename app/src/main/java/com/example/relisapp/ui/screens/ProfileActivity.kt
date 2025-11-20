package com.example.relisapp.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.relisapp.R
import com.example.relisapp.data.local.SessionManager
import com.example.relisapp.di.ViewModelProviderFactory
import com.example.relisapp.ui.theme.BluePrimary
import com.example.relisapp.ui.theme.ReLisAppTheme
import com.example.relisapp.ui.theme.ReLisAppTheme
import com.example.relisapp.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class ProfileActivity : ComponentActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var session: SessionManager

    // 1. Khai báo Launcher để nhận kết quả
    private val profileUpdateLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // 2. Xử lý kết quả trả về từ InfoProfileActivity
        if (result.resultCode == RESULT_OK) {
            val isUpdated = result.data?.getBooleanExtra("updated", false) ?: false
            if (isUpdated) {
                // 3. Nếu có cập nhật, gọi hàm load lại dữ liệu
                loadUserProfile()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        session = SessionManager(this)
        val factory = ViewModelProviderFactory.provideAuthViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        setContent {
            ReLisAppTheme {

                var username by remember { mutableStateOf("") }
                var email by remember { mutableStateOf("") }

                // ⭐ Đặt biến trạng thái để kích hoạt việc tải lại
                val refreshTrigger = remember { mutableStateOf(false) }

                // 4. Hàm load dữ liệu được bọc lại
                LaunchedEffect(Unit, refreshTrigger.value) { // Thêm refreshTrigger.value vào key
                    val userId = session.getUserId()
                    val user = viewModel.getUserById(userId)
                    if (user != null) {
                        username = user.username
                        email = user.email ?: ""
                    }
                }

                // Cung cấp hàm loadUserProfile để có thể gọi từ bên ngoài composable
                (this as ProfileActivity).loadUserProfile = {
                    refreshTrigger.value = !refreshTrigger.value // Đảo giá trị để kích hoạt lại LaunchedEffect
                }

                ProfileScreen(
                    username = username,
                    email = email,
                    onBackClick = { finish() },

                    onAccountInfoClick = {
                        // 5. Thay thế startActivity bằng profileUpdateLauncher.launch
                        val intent = Intent(this, InfoProfileActivity::class.java)
                        profileUpdateLauncher.launch(intent)
                    },

                    onChangePasswordClick = {
                        startActivity(Intent(this, ChangePasswordActivity::class.java))
                    },

                    onLogoutClick = {
                        session.logout()
                        val intent = Intent(this, StartActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                )
            }
        }
    }

    // ⭐ Thêm hàm này để kích hoạt tải lại
    var loadUserProfile: () -> Unit = {}

    // Hàm gọi để tải lại dữ liệu từ launcher
    private fun loadUserProfile() {
        // Gọi hàm đã được cung cấp trong setContent
        loadUserProfile.invoke()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    username: String,
    email: String,
    onBackClick: () -> Unit,
    onAccountInfoClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tài khoản") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar + username + email
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.outline_article_person_24),
                        contentDescription = "Avatar",
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = username,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                }
            }

            // Menu items
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    ProfileMenuItem(
                        icon = R.drawable.outline_article_person_24,
                        text = "Thông tin tài khoản",
                        onClick = onAccountInfoClick
                    )
                    Divider()
                    ProfileMenuItem(
                        icon = R.drawable.outline_admin_panel_settings_24,
                        text = "Đổi mật khẩu",
                        onClick = onChangePasswordClick
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onLogoutClick,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                Text("Đăng xuất", color = Color.White, fontSize = 16.sp)
            }


        }
    }
}

@Composable
fun ProfileMenuItem(icon: Int, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = text,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(id = R.drawable.outline_arrow_right_alt_24),
            contentDescription = "Next",
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ReLisAppTheme {
        ProfileScreen(
            username = "Nguyễn Hoàng Nam",
            email = "nghoangnam244@gmail.com",
            onBackClick = {},
            onAccountInfoClick = {},
            onChangePasswordClick = {},
            onLogoutClick = {}
        )
    }
}
