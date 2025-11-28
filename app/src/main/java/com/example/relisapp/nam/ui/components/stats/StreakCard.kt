package com.example.relisapp.nam.ui.screens.streak.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.relisapp.nam.ui.screens.streak.AnimatedFireIcon
import com.example.relisapp.nam.ui.screens.streak.AnimatedStreakNumber

@Composable
fun StreakCard(
    currentStreak: Int,
    longestStreak: Int,
    isStudiedToday: Boolean,
    isStreakAtRisk: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3E0)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedFireIcon(
                    isActive = true,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Chuỗi ngày học",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    AnimatedStreakNumber(
                        targetValue = currentStreak,
                        modifier = Modifier
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "🏆 Dài nhất: $longestStreak ngày",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = if (isStudiedToday) "📅 Đã học hôm nay" else "📅 Chưa học hôm nay",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isStudiedToday) Color(0xFF4CAF50) else Color.Gray
            )

            if (isStreakAtRisk && !isStudiedToday) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFE0E0), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "⚠ Chuỗi streak sắp mất! Hãy học hôm nay!",
                        color = Color(0xFFD32F2F),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
