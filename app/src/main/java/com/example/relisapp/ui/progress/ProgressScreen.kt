@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.relisapp.ui.progress

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry


// ===== Data model giả =====
data class ResultHistory(
    val id: Int,
    val lesson: String,
    val type: String, // Listening / Reading
    val score: Int,
    val date: String
)

@Composable
fun ProgressScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf("Listening") }

    // Mock dữ liệu
    val history = remember {
        listOf(
            ResultHistory(1, "Travel Listening 1", "Listening", 70, "2025-09-01"),
            ResultHistory(2, "Travel Listening 2", "Listening", 80, "2025-09-05"),
            ResultHistory(3, "Reading: Environment", "Reading", 65, "2025-09-07"),
            ResultHistory(4, "Reading: Health", "Reading", 75, "2025-09-12"),
            ResultHistory(5, "Travel Listening 3", "Listening", 90, "2025-09-15")
        )
    }

    val filteredHistory = history.filter { it.type == selectedTab }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📊 Progress & History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Toggle tab
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { selectedTab = "Listening" },
                    colors = ButtonDefaults.buttonColors(
                        if (selectedTab == "Listening") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Listening")
                }
                Button(
                    onClick = { selectedTab = "Reading" },
                    colors = ButtonDefaults.buttonColors(
                        if (selectedTab == "Reading") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Reading")
                }
            }

            // Bar Chart
            AndroidView(
                factory = { context ->
                    BarChart(context).apply {
                        description.isEnabled = false
                        axisRight.isEnabled = false
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        xAxis.setDrawGridLines(false)
                        axisLeft.axisMinimum = 0f
                        axisLeft.axisMaximum = 100f
                    }
                },
                update = { chart ->
                    val entries = filteredHistory.mapIndexed { index, result ->
                        BarEntry(index.toFloat(), result.score.toFloat())
                    }
                    val dataSet = BarDataSet(entries, "$selectedTab Progress").apply {
                        color = Color.rgb(78, 141, 245)
                        valueTextSize = 12f
                    }
                    chart.data = BarData(dataSet)
                    chart.invalidate()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(12.dp)
            )

            Divider()

            // History list
            LazyColumn(
                modifier = Modifier.padding(12.dp)
            ) {
                items(filteredHistory) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(item.lesson, fontSize = 16.sp)
                            Text("Score: ${item.score}", fontSize = 14.sp)
                            Text("Date: ${item.date}", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }
        }
    }
}
