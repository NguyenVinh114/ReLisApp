package com.example.relisapp.nam.ui.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relisapp.nam.database.entity.StudySession
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StreakCalendarCard(
    sessions: List<StudySession>,
    modifier: Modifier = Modifier
) {
    val last30 = remember(sessions) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        (0 until 30).map { offset ->
            val cal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_MONTH, -offset)
            }
            val dateStr = dateFormat.format(cal.time)
            val session = sessions.find { it.date == dateStr }
            Calendar30Day(
                date = dateStr,
                day = cal.get(Calendar.DAY_OF_MONTH),
                hasSession = session?.lessonsCompleted ?: 0 > 0,
                lessonCount = session?.lessonsCompleted ?: 0
            )
        }.reversed()
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Lịch sử 30 ngày", fontWeight = FontWeight.Bold)
                Text(
                    "${last30.count { it.hasSession }}/30 ngày",
                    color = Color(0xFFFF6B35),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.height(220.dp),
                userScrollEnabled = false
            ) {
                items(last30) { day ->
                    StreakCalendarDayItem(day = day)
                }
            }

            Spacer(Modifier.height(12.dp))

            Divider()

            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(Color(0xFFFF6B35), "Đã học", Icons.Default.CheckCircle)
                LegendItem(Color(0xFFEEEEEE), "Chưa học", Icons.Default.Circle)
            }
        }
    }
}

data class Calendar30Day(
    val date: String,
    val day: Int,
    val hasSession: Boolean,
    val lessonCount: Int
)

@Composable
fun StreakCalendarDayItem(day: Calendar30Day) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (day.hasSession) Color(0xFFFF6B35)
                else Color(0xFFEEEEEE)
            )
            .clickable { showDialog = true },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.day.toString(),
                color = if (day.hasSession) Color.White else Color.Gray
            )
            if (day.hasSession && day.lessonCount > 0) {
                Text(
                    text = "${day.lessonCount}",
                    fontSize = 10.sp,
                    color = Color.White,
                    modifier = Modifier
                        .background(Color(0xFFD84315), CircleShape)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        }
    }

    if (showDialog) {
        val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val output = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val textDate =
            output.format(input.parse(day.date) ?: Date())

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(textDate) },
            text = {
                Text(
                    if (day.hasSession)
                        "✅ Đã học ${day.lessonCount} bài"
                    else
                        "❌ Chưa học ngày này"
                )
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Đóng")
                }
            }
        )
    }
}

@Composable
fun LegendItem(color: Color, text: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, color = Color.Gray, fontSize = 12.sp)
    }
}
