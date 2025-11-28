package com.example.relisapp.nam.ui.screens.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relisapp.nam.database.entity.User
import com.example.relisapp.nam.viewmodel.LeaderboardViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import com.example.relisapp.nam.database.Converters.BitmapConverter
import com.example.relisapp.R
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel,
    currentUserId: Int,
    onBack: () -> Unit
) {
    val users by viewModel.topUsers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bảng Xếp Hạng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFD700).copy(alpha = 0.1f) // Màu vàng nhạt
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFFF6B35))
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFFD700).copy(alpha = 0.1f),
                                Color.White
                            )
                        )
                    )
            ) {
                // 1. TOP 3 PODIUM
                if (users.isNotEmpty()) {
                    TopThreePodium(users.take(3))
                }

                Spacer(Modifier.height(16.dp))

                // 2. LIST 4-13
                val restUsers = if (users.size > 3) users.drop(3) else emptyList()

                LazyColumn(
                    contentPadding = PaddingValues(bottom = 20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    itemsIndexed(restUsers) { index, user ->
                        LeaderboardItem(
                            rank = index + 4, // Bắt đầu từ 4
                            user = user,
                            isMe = user.userId == currentUserId
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopThreePodium(topUsers: List<User>) {
    val first = topUsers.getOrNull(0)
    val second = topUsers.getOrNull(1)
    val third = topUsers.getOrNull(2)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        // Hạng 2 (Trái)
        if (second != null) {
            PodiumItem(user = second, rank = 2, color = Color(0xFFC0C0C0), size = 90.dp)
        }

        Spacer(Modifier.width(16.dp))

        // Hạng 1 (Giữa - To nhất)
        if (first != null) {
            PodiumItem(user = first, rank = 1, color = Color(0xFFFFD700), size = 110.dp)
        }

        Spacer(Modifier.width(16.dp))

        // Hạng 3 (Phải)
        if (third != null) {
            PodiumItem(user = third, rank = 3, color = Color(0xFFCD7F32), size = 90.dp)
        }
    }
}

@Composable
fun PodiumItem(user: User, rank: Int, color: Color, size: androidx.compose.ui.unit.Dp) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Crown for #1
        if (rank == 1) {
            Icon(
                painter = painterResource(R.drawable.ic_crown), // Bạn cần thêm icon vương miện hoặc dùng icon có sẵn
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(24.dp)
            )
        } else {
            Spacer(Modifier.height(24.dp))
        }

        // Avatar
        Box(
            modifier = Modifier
                .size(size)
                .border(4.dp, color, CircleShape)
                .padding(4.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            val bitmap = user.avatar?.let { BitmapConverter.byteArrayToBitmap(it) }
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = user.username.first().uppercase(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }

            // Rank Badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 8.dp)
                    .background(color, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "#$rank",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = user.username,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(80.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Text(
            text = "🔥 ${user.currentStreak}",
            color = Color(0xFFFF6B35),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LeaderboardItem(rank: Int, user: User, isMe: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isMe) Color(0xFFE3F2FD) else Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#$rank",
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.width(32.dp)
            )

            // Avatar nhỏ
            val bitmap = user.avatar?.let { BitmapConverter.byteArrayToBitmap(it) }
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(user.username.first().uppercase(), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isMe) "${user.username} (Bạn)" else user.username,
                    fontWeight = FontWeight.Bold,
                    color = if (isMe) Color(0xFF1565C0) else Color.Black
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${user.currentStreak}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFFFF6B35)
                )
                Icon(
                    painter = painterResource(R.drawable.ic_fire),
                    contentDescription = null,
                    tint = Color(0xFFFF6B35),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}