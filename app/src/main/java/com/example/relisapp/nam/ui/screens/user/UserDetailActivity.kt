package com.example.relisapp.nam.ui.screens.user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.relisapp.nam.data.repository.UserRepository
import com.example.relisapp.nam.database.AppDatabase
import com.example.relisapp.nam.database.Converters.BitmapConverter
import com.example.relisapp.nam.database.entity.User
import com.example.relisapp.nam.ui.theme.BluePrimary
import com.example.relisapp.nam.ui.theme.GrayText
import com.example.relisapp.nam.ui.theme.LearnTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


class UserDetailActivity : ComponentActivity() {

    private lateinit var repository: UserRepository
    private var user by mutableStateOf<User?>(null)
    private var isLoading by mutableStateOf(true)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.Companion.getDatabase(this)
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
                            modifier = Modifier.Companion
                                .fillMaxSize()
                                .padding(padding),
                            contentAlignment = Alignment.Companion.Center
                        ) {
                            CircularProgressIndicator(color = BluePrimary)
                        }
                    } else {
                        user?.let { UserDetailContent(it, Modifier.Companion.padding(padding)) }
                            ?: NoUserFound(Modifier.Companion.padding(padding))
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
    private fun UserDetailContent(user: User, modifier: Modifier = Modifier.Companion) {
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
                modifier = Modifier.Companion
                    .size(120.dp)
                    .align(Alignment.Companion.CenterHorizontally)
            ) {
                if (avatarBitmap != null) {
                    Image(
                        bitmap = avatarBitmap.asImageBitmap(),
                        contentDescription = "Avatar người dùng",
                        contentScale = ContentScale.Companion.Crop,
                        modifier = Modifier.Companion.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.Companion
                            .fillMaxSize()
                            .background(BluePrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Companion.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = BluePrimary,
                            modifier = Modifier.Companion.size(60.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.Companion.height(8.dp))

            // User Info Card
            Card(
                modifier = Modifier.Companion.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.Companion.padding(16.dp),
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
                        valueColor = when (user.role) {
                            "admin" -> Color(0xFF1565C0)
                            "mod" -> Color(0xFFE65100)
                            else -> Color.Companion.Unspecified
                        }
                    )
                    DetailRow(
                        label = "Trạng thái",
                        value = when (user.accountStatus) {
                            "active" -> "Hoạt động"
                            "locked" -> "Bị khóa"
                            "inactive" -> "Không hoạt động"
                            else -> "-"
                        },
                        valueColor = when (user.accountStatus) {
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
                        value = if (user.isVerified == 1) "Đã xác thực" else "Chưa xác thực"
                    )
                }
            }

            Spacer(Modifier.Companion.weight(1f))

            // Action Buttons
            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { finish() },
                    modifier = Modifier.Companion.weight(1f)
                ) {
                    Icon(Icons.Default.Close, null)
                    Spacer(Modifier.Companion.width(8.dp))
                    Text("Đóng")
                }
            }
        }
    }

    @Composable
    private fun NoUserFound(modifier: Modifier = Modifier.Companion) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Companion.Center
        ) {
            Column(
                horizontalAlignment = Alignment.Companion.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Default.PersonOff,
                    contentDescription = null,
                    modifier = Modifier.Companion.size(80.dp),
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
        valueColor: Color = Color.Companion.Unspecified
    ) {
        Row(
            modifier = Modifier.Companion.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Companion.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = GrayText,
                fontWeight = FontWeight.Companion.Medium
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Companion.SemiBold,
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