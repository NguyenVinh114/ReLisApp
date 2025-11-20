package com.example.relisapp.phat.ui.admin

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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relisapp.R

class AdminDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AdminDashboardScreen(
                    onManageCategories = {
                        startActivity(Intent(this, CategoryListActivity::class.java))
                    },
                    onManageLessons = {
                        startActivity(Intent(this, AddLessonActivity::class.java))
                    },
                    onManageUsers = {
                        Toast.makeText(this, "Manage Users clicked", Toast.LENGTH_SHORT).show()
                    },
                    onFeedback = {
                        Toast.makeText(this, "Feedback clicked", Toast.LENGTH_SHORT).show()
                    },
                    onLogout = {
                        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                        // finish()
                    }
                )
            }
        }
    }
}

@Composable
fun AdminDashboardScreen(
    onManageCategories: () -> Unit,
    onManageLessons: () -> Unit,
    onManageUsers: () -> Unit,
    onFeedback: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🔹 Admin Info (avatar + email)
        AdminInfoCard()

        Spacer(Modifier.height(20.dp))

        // 🔹 Header
        Text(
            text = "System Administration",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )

        Spacer(Modifier.height(20.dp))

        // 🔹 Dashboard Items
        DashboardCard(
            title = "Manage Categories",
            iconRes = R.drawable.ic_category,
            backgroundColor = Color(0xFF3F51B5),
            onClick = onManageCategories
        )

        DashboardCard(
            title = "Manage Lessons",
            iconRes = R.drawable.ic_assignment,
            backgroundColor = Color(0xFF2196F3),
            onClick = onManageLessons
        )

        DashboardCard(
            title = "Manage Users",
            iconRes = R.drawable.ic_user,
            backgroundColor = Color(0xFF009688),
            onClick = onManageUsers
        )

        DashboardCard(
            title = "Feedback & Reviews",
            iconRes = R.drawable.ic_feedback,
            backgroundColor = Color(0xFFFF5722),
            onClick = onFeedback
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 🔹 Logout Button
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Logout",
                tint = Color.White
            )
            Spacer(Modifier.width(8.dp))
            Text("Logout", color = Color.White, fontSize = 16.sp)
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    iconRes: Int,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(backgroundColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

@Composable
fun AdminInfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar placeholder (can replace with real image)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3F51B5)),
                contentAlignment = Alignment.Center
            ) {
                Text("A", fontSize = 28.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            Text("Admin", style = MaterialTheme.typography.titleMedium)
            Text("admin@example.com", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}
