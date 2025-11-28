package com.example.relisapp.nam.ui.screens.streak

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.relisapp.nam.di.StreakViewModelFactory
import com.example.relisapp.nam.ui.components.calendar.MonthYearPickerBottomSheet
import com.example.relisapp.nam.ui.components.calendar.MonthlyCalendar
import com.example.relisapp.nam.viewmodel.StreakViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: StreakViewModel = viewModel(factory = StreakViewModelFactory(context))

    // --- MONTH PICKER STATE ---
    var showPicker by remember { mutableStateOf(false) }

    // Lấy ngày tháng hiện tại
    val calendar = Calendar.getInstance()
    var selectedMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH) + 1) } // Tháng 1-12
    var selectedYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }

    val state by viewModel.uiState.collectAsState()

    // ⭐ [FIX] Tự động tải dữ liệu tháng khi màn hình mở hoặc khi thay đổi tháng/năm
    LaunchedEffect(selectedMonth, selectedYear) {
        viewModel.loadMonth(selectedYear, selectedMonth)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Streak") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { showPicker = true }) {
                        Icon(Icons.Filled.DateRange, contentDescription = "Pick Month")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ---------- SUMMARY ----------
            Text("🔥 Current streak: ${state.currentStreak}", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("🏆 Longest streak: ${state.longestStreak}")
            Spacer(Modifier.height(8.dp))
            Text("📘 Total study days: ${state.totalDays}")
            Spacer(Modifier.height(24.dp))

            // ---------- MONTH SELECTED ----------
            Text(
                text = "📅 Tháng $selectedMonth / $selectedYear",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(16.dp))

            // ---------- MONTHLY CALENDAR ----------
            MonthlyCalendar(
                year = selectedYear,
                month = selectedMonth,
                sessions = state.calendarSessions,
                onDayClick = { date ->
                    println("Day clicked: $date")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    // Chuyển sang LeaderboardActivity
                    val intent = android.content.Intent(context, com.example.relisapp.nam.ui.screens.leaderboard.LeaderboardActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD700) // Màu vàng Gold
                ),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color.Black
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Bảng Xếp Hạng Top 3",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(16.dp))

        }
    }

    // ---------- MONTH/YEAR PICKER ----------
    if (showPicker) {
        MonthYearPickerBottomSheet(
            currentMonth = selectedMonth,
            currentYear = selectedYear,
            onDismiss = { showPicker = false },
            onApply = { m, y ->
                selectedMonth = m
                selectedYear = y
                // Không cần gọi viewModel.loadMonth ở đây nữa
                // vì LaunchedEffect ở trên sẽ tự chạy khi biến selectedMonth/Year thay đổi
            }
        )
    }


}