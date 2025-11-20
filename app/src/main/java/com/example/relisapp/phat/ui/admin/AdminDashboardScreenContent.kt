package com.example.relisapp.phat.ui.admin

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

@Composable
fun AdminDashboardScreenContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AdminInfoCard()

        Spacer(Modifier.height(20.dp))

        Text(
            text = "System Administration",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )

        Spacer(Modifier.height(20.dp))

        DashboardCard(
            title = "Manage Categories",
            iconRes = R.drawable.ic_category,
            backgroundColor = Color(0xFF3F51B5)
        ) {}

        DashboardCard(
            title = "Manage Lessons",
            iconRes = R.drawable.ic_assignment,
            backgroundColor = Color(0xFF2196F3)
        ) {}

        DashboardCard(
            title = "Manage Users",
            iconRes = R.drawable.ic_user,
            backgroundColor = Color(0xFF009688)
        ) {}

        DashboardCard(
            title = "Feedback & Reviews",
            iconRes = R.drawable.ic_feedback,
            backgroundColor = Color(0xFFFF5722)
        ) {}

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { /* logout logic */ },
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
