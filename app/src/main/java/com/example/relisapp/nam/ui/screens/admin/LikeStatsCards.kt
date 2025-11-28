package com.example.relisapp.nam.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.relisapp.nam.viewmodel.LessonLikeStats
import com.example.relisapp.nam.viewmodel.RecentLikeItem
import com.example.relisapp.nam.viewmodel.UserLikeStats

// --- CARD 1: TỔNG QUAN ---
@Composable
fun TotalLikesCard(total: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Tổng lượt thích", style = MaterialTheme.typography.titleMedium)
                Text(
                    "$total",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

// --- CARD 2: TOP BÀI HỌC ---
@Composable
fun TopLessonsCard(list: List<LessonLikeStats>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, null, tint = Color(0xFFFFD700))
                Spacer(Modifier.width(8.dp))
                Text("Top Bài Học Hot", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))

            if (list.isEmpty()) Text("Chưa có dữ liệu", fontStyle = FontStyle.Italic)

            list.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${index + 1}. ${item.lessonTitle}", modifier = Modifier.weight(1f))
                    Text("${item.count} ❤", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                if (index < list.size - 1) Divider(color = Color.LightGray.copy(alpha = 0.3f))
            }
        }
    }
}

// --- CARD 3: TOP USER ---
@Composable
fun TopUsersCard(list: List<UserLikeStats>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.width(8.dp))
                Text("Top Người Dùng Tích Cực", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))

            if (list.isEmpty()) Text("Chưa có dữ liệu")

            list.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${index + 1}. ${item.userName}", modifier = Modifier.weight(1f))
                    Text("${item.count} ❤", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

// --- CARD 4: MỚI NHẤT ---
@Composable
fun RecentLikesCard(list: List<RecentLikeItem>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            // Tiêu đề Card
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.History, null, tint = Color.Gray)
                Spacer(Modifier.width(8.dp))
                Text("Hoạt Động Gần Đây", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))

            if (list.isEmpty()) Text("Chưa có dữ liệu", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

            // Danh sách item
            list.forEach { item ->
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.Top // Căn chỉnh lên trên nếu text dài
                ) {
                    // Dấu chấm tròn xanh điểm nhấn
                    Text("• ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleLarge)

                    Column {
                        // Dòng 1: Tên User (Đậm) + hành động
                        Text(
                            text = buildString {
                                append(item.userName) // Tên user
                                append(" đã thích bài:")
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold // Làm đậm tên user cho dễ nhìn
                        )

                        // Dòng 2: Tên bài học
                        Text(
                            text = item.lessonTitle,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Dòng 3: Thời gian (nhạt màu)
                        Text(
                            text = item.time,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
                Divider(modifier = Modifier.padding(start = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
            }
        }
    }
}