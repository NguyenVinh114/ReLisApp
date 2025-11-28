package com.example.relisapp.nam.ui.components.milestone

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import com.example.relisapp.nam.model.StreakMilestone

// =====================================================================
// 📌 CARD DANH SÁCH MILESTONE
// =====================================================================
@Composable
fun MilestonesCard(
    currentStreak: Int,
    achievedMilestones: List<StreakMilestone>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Các mốc thành tích",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            StreakMilestone.entries.forEachIndexed { index, milestone ->

                val isAchieved = milestone in achievedMilestones

                val isCurrent =
                    !isAchieved &&
                            (index == 0 ||
                                    achievedMilestones.contains(StreakMilestone.entries[index - 1]))

                MilestoneItem(
                    milestone = milestone,
                    isAchieved = isAchieved,
                    isCurrent = isCurrent,
                    currentStreak = currentStreak
                )

                if (index < StreakMilestone.entries.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}


// =====================================================================
// 📌 ITEM — MỖI MILESTONE
// =====================================================================
@Composable
fun MilestoneItem(
    milestone: StreakMilestone,
    isAchieved: Boolean,
    isCurrent: Boolean,
    currentStreak: Int
) {
    val emojiBackground = when {
        isAchieved -> Color(0xFFE8F5E9)
        isCurrent -> Color(0xFFFFF3E0)
        else -> Color(0xFFF5F5F5)
    }

    val emojiAlpha = if (isAchieved || isCurrent) 1f else 0.4f

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // ------------ Icon emoji trái ------------
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(MaterialTheme.shapes.large)
                .background(emojiBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = milestone.emoji,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.alpha(emojiAlpha)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // ------------ Nội dung ------------
        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = milestone.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = when {
                    isAchieved -> Color.Black
                    isCurrent -> Color(0xFFFF6B35)
                    else -> Color.Gray
                }
            )

            Text(
                text = milestone.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            // ------------ Progress cho mốc hiện tại ------------
            if (isCurrent && currentStreak < milestone.days) {
                Spacer(modifier = Modifier.height(8.dp))

                val progress = (currentStreak.toFloat() / milestone.days)
                    .coerceIn(0f, 1f)

                LinearProgressIndicator(
                    progress = progress,
                    color = Color(0xFFFF6B35),
                    trackColor = Color(0xFFFFE0B2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Còn ${milestone.days - currentStreak} ngày nữa",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6B35)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // ------------ Icon trạng thái bên phải ------------
        if (isAchieved) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(Color(0xFF4CAF50)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(Color(0xFFEEEEEE)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = milestone.days.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        }
    }
}
