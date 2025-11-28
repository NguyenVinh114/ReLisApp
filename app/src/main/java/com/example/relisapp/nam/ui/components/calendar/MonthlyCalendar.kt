package com.example.relisapp.nam.ui.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.relisapp.nam.database.entity.StudySession
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MonthlyCalendar(
    year: Int,
    month: Int,
    sessions: List<StudySession>,
    onDayClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val firstCalendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    val daysInMonth = firstCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = (firstCalendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Monday = 0

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header T2–CN
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("T2","T3","T4","T5","T6","T7","CN").forEach {
                    Text(
                        text = it,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.heightIn(min = 320.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Empty before day 1
                items(firstDayOfWeek) {
                    Box(modifier = Modifier.size(44.dp))
                }

                items(daysInMonth) { index ->
                    val day = index + 1

                    val dateStr = "%04d-%02d-%02d".format(year, month, day)

                    val session = sessions.find { it.date == dateStr }
                    val hasSession = session != null
                    val lessonCount = session?.lessonsCompleted ?: 0

                    CalendarDayBox(
                        day = day,
                        hasSession = hasSession,
                        lessonCount = lessonCount,
                        onClick = { onDayClick(dateStr) }
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarDayBox(
    day: Int,
    hasSession: Boolean,
    lessonCount: Int,
    onClick: () -> Unit
) {
    val bgColor = when {
        hasSession -> Color(0xFFFF6B35)
        else -> Color(0xFFF2F2F2)
    }

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(bgColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = day.toString(),
                color = if (hasSession) Color.White else Color.Gray,
                fontWeight = FontWeight.Medium
            )

            // badge nhỏ
            if (hasSession && lessonCount > 0) {
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(Color(0xFFD84315))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        "$lessonCount",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
