package com.example.relisapp.nam.ui.components.milestone

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relisapp.nam.model.StreakMilestone

@Composable
fun MilestoneProgressCard(
    currentStreak: Int,
    nextMilestone: StreakMilestone?
) {
    if (nextMilestone == null) return

    val progress = (currentStreak.toFloat() / nextMilestone.days)
        .coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Tiến độ đến mốc kế tiếp",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                // ⭐ Emoji milestone (phóng to một chút)
                Text(
                    text = nextMilestone.emoji,
                    fontSize = 40.sp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {

                    // Tiêu đề milestone
                    Text(
                        text = nextMilestone.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Thanh tiến độ
                    LinearProgressIndicator(
                        progress = progress,
                        color = Color(0xFFFF6B35),
                        trackColor = Color(0xFFFFD5B0),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Số ngày còn lại
                    Text(
                        text = "Còn ${nextMilestone.days - currentStreak} ngày nữa để đạt mốc này",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
