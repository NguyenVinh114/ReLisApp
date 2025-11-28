package com.example.relisapp.nam.ui.screens.user

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
import com.example.relisapp.nam.ui.screens.settings.SettingsActivity
import com.example.relisapp.nam.ui.screens.home.StartActivity
import com.example.relisapp.nam.ui.screens.streak.StreakActivity
import com.example.relisapp.nam.ui.screens.auth.ChangePassWordActivity
import com.example.relisapp.nam.viewmodel.AuthViewModel

class ProfileActivity : ComponentActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Khởi tạo SessionManager
        session = SessionManager(this)
        val userId = session.getUserId()

        // ✅ FIX LỖI 2: Kiểm tra session hợp lệ
        if (userId == -1) {
            Toast.makeText(this, "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show()
            session.logout()

            val intent = Intent(this, StartActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        // Inject ViewModel
        val factory = ViewModelProviderFactory.provideAuthViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        // ✅ FIX LỖI 3: Load user qua ViewModel (MVVM chuẩn)
        viewModel.loadUser(userId)

        setContent {
            LearnTheme {
                // ✅ FIX LỖI 3: Observe StateFlow từ ViewModel
                val user by viewModel.currentUser.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()

                ProfileScreen(
                    user = user,
                    isLoading = isLoading,
                    onBackClick = { finish() },
                    onAccountInfoClick = {
                        startActivity(Intent(this, InfoProfileActivity::class.java))
                    },
                    onChangePasswordClick = {
                        startActivity(Intent(this, ChangePassWordActivity::class.java))
                    },
                    onLogoutClick = {
                        // 🔶 FIX (2): Clear current user state trong ViewModel
                        viewModel.clearCurrentUser()

                        session.logout()

                        // ✅ FIX LỖI 6: Clear back stack khi logout
                        val intent = Intent(this, StartActivity::class.java).apply {
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val userId = session.getUserId()
        if (userId != -1) {
            viewModel.loadUser(userId)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: User?,
    isLoading: Boolean = false,
    onBackClick: () -> Unit,
    onAccountInfoClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tài khoản") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        // ✅ FIX LỖI 4: Hiển thị loading state
        if (isLoading) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BluePrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar + Username Card
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
                        // ✅ FIX LỖI 5: Load avatar từ database
                        ProfileAvatar(user = user)

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = user?.username ?: "Người dùng",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Menu Items Card
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
                        HorizontalDivider()

                        ProfileMenuItem(
                            icon = R.drawable.outline_admin_panel_settings_24,
                            text = "Đổi mật khẩu",
                            onClick = onChangePasswordClick
                        )
                        HorizontalDivider()

                        // ✅ THÊM STREAK Ở ĐÂY
                        ProfileMenuItem(
                            icon = R.drawable.ic_fire,     // icon streak
                            text = "Streak học tập",
                            onClick = {
                                context.startActivity(
                                    Intent(context, StreakActivity::class.java)
                                )
                            }
                        )
                        HorizontalDivider()

                        ProfileMenuItem(
                            icon = R.drawable.ic_notification,   // dùng icon notification
                            text = "Cài đặt thông báo",
                            onClick = {
                                context.startActivity(
                                    Intent(context, SettingsActivity::class.java)
                                )
                            }
                        )
                    }
                }


                Spacer(modifier = Modifier.weight(1f))

                // Logout Button
                Button(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Spacer(Modifier.width(8.dp))
                    Text("Đăng xuất", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ProfileAvatar(
    user: User?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        // Kiểm tra nếu user có avatar trong database
        val avatarBitmap = user?.avatar?.let { bytes ->
            BitmapConverter.byteArrayToBitmap(bytes)
        }

        if (avatarBitmap != null) {
            // Hiển thị avatar từ database
            Image(
                bitmap = avatarBitmap.asImageBitmap(),
                contentDescription = "Avatar",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Hiển thị icon mặc định
            Image(
                painter = painterResource(id = R.drawable.outline_article_person_24),
                contentDescription = "Default Avatar",
                modifier = Modifier.size(50.dp),
                colorFilter = ColorFilter.tint(
                    MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

/**
 * Menu item component
 */
@Composable
fun ProfileMenuItem(
    icon: Int,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = text,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface
        )
        Icon(
            painter = painterResource(id = R.drawable.outline_arrow_right_alt_24),
            contentDescription = "Next",
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ================================================================================================
// PREVIEW
// ================================================================================================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    LearnTheme {
        ProfileScreen(
            user = User(
                userId = 1,
                username = "nguyenhoangnam",
                password = "",
                fullName = "Nguyễn Hoàng Nam",
                email = "nam.nguyen@example.com",
                phoneNumber = "+84901234567",
                avatar = null,
                role = "user",
                accountStatus = "active",
                isVerified = 1
            ),
            isLoading = false,
            onBackClick = {},
            onAccountInfoClick = {},
            onChangePasswordClick = {},
            onLogoutClick = {}
        )
    }
}