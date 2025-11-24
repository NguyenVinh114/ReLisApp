package com.example.relisapp.ui.user.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.relisapp.data.local.entity.model.ResultWithLessonInfo
import com.example.relisapp.ui.viewmodel.ProgressViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel,
    userId: Int,
    onBack: () -> Unit
) {
    LaunchedEffect(userId) {
        viewModel.loadRealData(userId)
    }

    val history by viewModel.studyHistory.collectAsState()
    val listeningEntries by viewModel.listeningChartEntries.collectAsState()
    val readingEntries by viewModel.readingChartEntries.collectAsState()
    val listeningLabels by viewModel.listeningLabels.collectAsState()
    val readingLabels by viewModel.readingLabels.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    val currentEntries = if (selectedTab == 0) listeningEntries else readingEntries
    val dateLabels = if (selectedTab == 0) listeningLabels else readingLabels

    // ChartEntryModelProducer phải cập nhật khi data thay đổi
    val chartProducer = remember { ChartEntryModelProducer(listOf<FloatEntry>()) }
    LaunchedEffect(currentEntries) {
        chartProducer.setEntries(currentEntries)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Progress", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF5F5F5),
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // 1. CHART CARD
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth().height(320.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.White,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Listening") })
                        Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Reading") })
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (currentEntries.isNotEmpty()) {
                        Chart(
                            chart = columnChart(),
                            chartModelProducer = chartProducer,
                            startAxis = rememberStartAxis(
                                title = "Score (0-10)",
                                valueFormatter = { value, _ -> value.toInt().toString() }
                            ),
                            bottomAxis = rememberBottomAxis(
                                title = "Date",
                                valueFormatter = { value, _ ->
                                    dateLabels.getOrNull(value.toInt()) ?: ""
                                }
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No data yet. Start learning!", color = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. HISTORY LIST
            Text(
                "Recent History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.primary
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(history) { item ->
                    HistoryItem(item)
                }
            }
        }
    }
}

@Composable
fun HistoryItem(item: ResultWithLessonInfo) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.lessonTitle,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )

                // Format ngày từ String ISO yyyy-MM-dd → dd/MM/yyyy
                val formattedDate = runCatching {
                    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    formatter.format(parser.parse(item.createdAt) ?: Date())
                }.getOrDefault(item.createdAt)

                Text(
                    text = "${item.lessonType.uppercase()} • $formattedDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Badge điểm
            val score = item.score?.toFloat() ?: 0f
            val total = item.totalQuestions?.toFloat() ?: 1f
            val passed = (score / total) >= 0.5f

            Surface(
                color = if (passed) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${item.score}/${item.totalQuestions}",
                    color = if (passed) Color(0xFF2E7D32) else Color(0xFFC62828),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
