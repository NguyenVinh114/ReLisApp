@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.relisapp.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --------- Model (demo) ----------
data class LessonItem(
    val id: Int,
    val title: String,
    val description: String,
    val topic: String,
    val level: String,       // "Beginner" | "Intermediate" | "Advanced"
    val durationMinutes: Int // integer minutes
)

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onLessonClick: (LessonItem) -> Unit = {}
) {
    // demo data
    val sampleLessons = remember {
        listOf(
            LessonItem(1, "Travel Listening 1", "Short dialog at the airport.", "Travel", "Beginner", 4),
            LessonItem(2, "Environment Reading", "Short passage about recycling.", "Environment", "Intermediate", 8),
            LessonItem(3, "Health Listening", "Conversation about healthy food.", "Health", "Beginner", 6),
            LessonItem(4, "Daily Life Listening", "Daily routine conversation.", "Daily Life", "Advanced", 12),
            LessonItem(5, "Technology Reading", "Article about AI.", "Technology", "Advanced", 15),
            LessonItem(6, "Shopping Listening", "Shopping phrases and expressions.", "Shopping", "Intermediate", 5)
        )
    }

    val allTopics = remember { sampleLessons.map { it.topic }.distinct() }
    val levels = listOf("All levels", "Beginner", "Intermediate", "Advanced")
    val durations = listOf("< 5 min", "5 - 10 min", "> 10 min")

    var query by remember { mutableStateOf("") }
    val selectedTopics = remember { mutableStateListOf<String>() }
    var selectedLevel by remember { mutableStateOf("All levels") }
    var selectedDuration by remember { mutableStateOf<String?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val filtered by remember(query, selectedTopics, selectedLevel, selectedDuration) {
        derivedStateOf {
            sampleLessons.filter { lesson ->
                val keywordOk = query.isBlank() || (lesson.title + " " + lesson.description).contains(query, ignoreCase = true)
                val topicOk = selectedTopics.isEmpty() || selectedTopics.contains(lesson.topic)
                val levelOk = selectedLevel == "All levels" || lesson.level == selectedLevel
                val durationOk = when (selectedDuration) {
                    "< 5 min" -> lesson.durationMinutes < 5
                    "5 - 10 min" -> lesson.durationMinutes in 5..10
                    "> 10 min" -> lesson.durationMinutes > 10
                    null -> true
                    else -> true
                }
                keywordOk && topicOk && levelOk && durationOk
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search lessons") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF6F7FB))
        ) {
            // Search box
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                placeholder = { Text("Search lessons...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )

            // Filters
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Topic", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    allTopics.forEach { topic ->
                        val selected = selectedTopics.contains(topic)
                        FilterChip(
                            selected = selected,
                            onClick = {
                                if (selected) selectedTopics.remove(topic) else selectedTopics.add(topic)
                            },
                            label = { Text(topic) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Level dropdown + Duration chips
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = selectedLevel,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Level") },
                            trailingIcon = {
                                IconButton(onClick = { dropdownExpanded = true }) {
                                    Icon(Icons.Filled.ArrowBack, contentDescription = null, modifier = Modifier.rotate(90f))
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { dropdownExpanded = true }
                        )
                        DropdownMenu(expanded = dropdownExpanded, onDismissRequest = { dropdownExpanded = false }) {
                            levels.forEach { lv ->
                                DropdownMenuItem(
                                    text = { Text(lv) },
                                    onClick = {
                                        selectedLevel = lv
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Duration", fontSize = 12.sp)
                        Spacer(Modifier.height(6.dp))
                        Row {
                            durations.forEach { d ->
                                FilterChip(
                                    selected = selectedDuration == d,
                                    onClick = {
                                        selectedDuration = if (selectedDuration == d) null else d
                                    },
                                    label = { Text(d) },
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            Divider()

            // Results
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (filtered.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("No lessons found", color = Color.Gray)
                        }
                    }
                } else {
                    items(filtered) { lesson ->
                        LessonCardDetailed(lesson, onClick = { onLessonClick(lesson) })
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonCardDetailed(lesson: LessonItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(lesson.title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(Modifier.height(6.dp))
            Text(lesson.description, maxLines = 2, fontSize = 13.sp, color = Color.Gray)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(lesson.topic, fontSize = 12.sp, color = Color(0xFF333333))
                Spacer(Modifier.width(8.dp))
                Text("•", color = Color.Gray)
                Spacer(Modifier.width(8.dp))
                Text(lesson.level, fontSize = 12.sp)
                Spacer(Modifier.width(12.dp))
                Text("${lesson.durationMinutes} min", fontSize = 12.sp, color = Color.DarkGray)
            }
        }
    }
}
