package com.example.relisapp.phat.ui.admin.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.relisapp.R

@Composable
fun AdminDashboardScreenContent(
    modifier: Modifier = Modifier,
    onManageCategories: () -> Unit,
    onManageLessons: () -> Unit,
    onManageUsers: () -> Unit,
    onFeedback: () -> Unit,
    onLogout: () -> Unit
) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.surfaceVariant
        ),
        startY = 0.0f,
        endY = 800.0f
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(gradientBrush)
            .verticalScroll(rememberScrollState()) // Giữ lại cuộn cho toàn màn hình
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- PHẦN HEADER ---
        AdminInfoCard()
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Welcome Back, Admin!",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(Modifier.height(24.dp))

        // --- PHẦN THỐNG KÊ (STATS) ---
        Text(
            text = "Overview",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                label = "Total Users",
                value = "1,250",
                icon = Icons.Default.People,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Total Lessons",
                value = "320",
                icon = Icons.Default.LibraryBooks,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(24.dp))

        // --- PHẦN QUẢN LÝ ---
        Text(
            text = "System Administration",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(Modifier.height(16.dp))

        // ***** SỬA LỖI Ở ĐÂY: DÙNG 2 ROW THAY VÌ LAZYVERTICALGRID *****
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp) // Khoảng cách giữa 2 hàng
        ) {
            // Hàng thứ nhất
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp), // Khoảng cách giữa 2 cột
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    GridDashboardCard(
                        title = "Categories",
                        iconRes = R.drawable.ic_category,
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        onClick = onManageCategories
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    GridDashboardCard(
                        title = "Lessons",
                        iconRes = R.drawable.ic_assignment,
                        backgroundColor = MaterialTheme.colorScheme.secondary,
                        onClick = onManageLessons
                    )
                }
            }
            // Hàng thứ hai
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    GridDashboardCard(
                        title = "Users",
                        iconRes = R.drawable.ic_user,
                        backgroundColor = Color(0xFF009688),
                        onClick = onManageUsers
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    GridDashboardCard(
                        title = "Feedback",
                        iconRes = R.drawable.ic_feedback,
                        backgroundColor = Color(0xFFFF5722),
                        onClick = onFeedback
                    )
                }
            }
        }


        // Spacer(modifier = Modifier.weight(1f)) // Không cần thiết nữa vì không dùng LazyColumn/LazyGrid
        Spacer(modifier = Modifier.height(32.dp))

        // --- NÚT LOGOUT ---
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Logout"
            )
            Spacer(Modifier.width(8.dp))
            Text("Logout")
        }
    }
}

// Composable MỚI cho thẻ thống kê
@Composable
fun StatCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}


// Composable MỚI cho thẻ dạng lưới
@Composable
fun GridDashboardCard(
    title: String,
    iconRes: Int,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f) // Giữ cho thẻ luôn vuông
            .fillMaxWidth() // Thêm fillMaxWidth để nó lấp đầy Box cha
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


// Composable cũ AdminInfoCard có thể giữ nguyên
@Composable
fun AdminInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text( "A", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Administrator", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                Text("admin@relisapp.com", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
