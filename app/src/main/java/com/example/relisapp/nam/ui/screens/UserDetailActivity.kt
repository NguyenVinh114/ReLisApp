package com.example.relisapp.nam.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.relisapp.nam.database.AppDatabase
import com.example.relisapp.nam.database.entity.User
import com.example.relisapp.nam.ui.theme.BluePrimary
import com.example.relisapp.nam.ui.theme.GrayText
import com.example.relisapp.nam.ui.theme.LearnTheme
import com.example.relisapp.nam.data.repository.UserRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.background
import com.example.relisapp.nam.database.Converters.BitmapConverter


class UserDetailActivity : ComponentActivity() {

    private lateinit var repository: UserRepository
    private var user by mutableStateOf<User?>(null)
    private var isLoading by mutableStateOf(true)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        repository = UserRepository(database.userDao())

        val userId = intent.getIntExtra("USER_ID", -1)

        if (userId != -1) {
            loadUserDetail(userId)
        } else {
            finish()
        }

        setContent {
            LearnTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Chi tiết người dùng") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                ) { padding ->
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = BluePrimary)
                        }
                    } else {
                        user?.let { UserDetailContent(it, Modifier.padding(padding)) }
                            ?: NoUserFound(Modifier.padding(padding))
                    }
                }
            }
        }
    }

    private fun loadUserDetail(userId: Int) {
        lifecycleScope.launch {
            try {
                user = repository.getUserById(userId)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    @Composable
    private fun UserDetailContent(user: User, modifier: Modifier = Modifier) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar
            val avatarBitmap = user.avatar?.let { BitmapConverter.byteArrayToBitmap(it) }

            Surface(
                shape = RoundedCornerShape(100),
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                if (avatarBitmap != null) {
                    Image(
                        bitmap = avatarBitmap.asImageBitmap(),
                        contentDescription = "Avatar người dùng",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(BluePrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = BluePrimary,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // User Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DetailRow(label = "ID", value = user.userId.toString())
                    DetailRow(label = "Username", value = user.username)
                    DetailRow(label = "Họ tên", value = user.fullName ?: "-")
                    DetailRow(label = "Email", value = user.email ?: "-")
                    DetailRow(label = "Số điện thoại", value = user.phoneNumber ?: "-")
                    DetailRow(
                        label = "Vai trò",
                        value = user.role?.uppercase() ?: "-",
                        valueColor = when(user.role) {
                            "admin" -> Color(0xFF1565C0)
                            "mod" -> Color(0xFFE65100)
                            else -> Color.Unspecified
                        }
                    )
                    DetailRow(
                        label = "Trạng thái",
                        value = when(user.accountStatus) {
                            "active" -> "Hoạt động"
                            "locked" -> "Bị khóa"
                            "inactive" -> "Không hoạt động"
                            else -> "-"
                        },
                        valueColor = when(user.accountStatus) {
                            "active" -> Color(0xFF2E7D32)
                            "locked" -> Color(0xFFC62828)
                            else -> GrayText
                        }
                    )
                    DetailRow(label = "Trình độ", value = user.level ?: "-")
                    DetailRow(label = "Tuổi", value = user.age?.toString() ?: "-")
                    user.createdAt?.let {
                        DetailRow(label = "Ngày tạo", value = formatDate(it))
                    }
                    DetailRow(
                        label = "Xác thực",
                        value = if (user.isVerified == true) "Đã xác thực" else "Chưa xác thực"
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { finish() },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Close, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Đóng")
                }
            }
        }
    }

    @Composable
    private fun NoUserFound(modifier: Modifier = Modifier) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Default.PersonOff,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = GrayText
                )
                Text("Không tìm thấy người dùng", color = GrayText)
            }
        }
    }

    @Composable
    private fun DetailRow(
        label: String,
        value: String,
        valueColor: Color = Color.Unspecified
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = GrayText,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = valueColor
            )
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = parser.parse(dateString)
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            formatter.format(date ?: Date())
        } catch (_: Exception) {
            dateString // Trả về string gốc nếu parse lỗi
        }
    }
}