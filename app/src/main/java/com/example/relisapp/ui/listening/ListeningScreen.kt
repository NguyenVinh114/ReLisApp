@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.relisapp.ui.listening

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ===== Models =====
data class ListeningQuestion(
    val id: Int,
    val question: String,
    val options: List<String>? = null, // null nếu là điền từ
    val correctAnswer: String
)

data class Feedback(
    val user: String,
    val comment: String
)

@Composable
fun ListeningScreen(onBack: () -> Unit) {
    var isTranscriptVisible by remember { mutableStateOf(false) }
    var isSubmitted by remember { mutableStateOf(false) }

    // ✅ Transcript + Question mẫu
    val transcript = "Tom is talking about his vacation to Japan. He visited Tokyo and Kyoto..."
    val questions = remember {
        listOf(
            ListeningQuestion(
                1, "Where did Tom go on vacation?",
                listOf("China", "Japan", "Korea", "Thailand"),
                "Japan"
            ),
            ListeningQuestion(
                2, "Fill in the blank: He visited _____ and Kyoto.",
                null,
                "Tokyo"
            )
        )
    }

    // ✅ Lưu đáp án người dùng
    val userAnswers = remember { mutableStateMapOf<Int, String>() }

    // ===== Feedback luôn hiển thị + mẫu sẵn =====
    var feedbackList by remember {
        mutableStateOf(
            listOf(
                Feedback("Alice", "Great lesson, very helpful!"),
                Feedback("Bob", "I liked the transcript feature."),
                Feedback("Carol", "The audio was clear and easy to follow."),
                Feedback("Dave", "Good pace and examples.")
            )
        )
    }
    var commentText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎧 Unit 1 – Travel Listening") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF9F9F9)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 🎧 Audio Player
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎧 Audio Player (Demo)", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { /* TODO: play audio */ }) {
                            Text("▶ Play / Pause")
                        }
                    }
                }
            }

            // 📜 Transcript toggle
            item {
                Button(onClick = { isTranscriptVisible = !isTranscriptVisible }) {
                    Text(if (isTranscriptVisible) "Hide Transcript" else "Show Transcript")
                }
                if (isTranscriptVisible) {
                    Spacer(Modifier.height(8.dp))
                    Text(transcript, fontSize = 16.sp)
                }
            }

            // 🧠 Questions
            items(questions) { q ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Q${q.id}: ${q.question}", fontSize = 16.sp, fontWeight = FontWeight.Bold)

                        if (q.options != null) {
                            q.options.forEach { option ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(
                                            when {
                                                isSubmitted && option == q.correctAnswer -> Color(0xFFDFFFE0)
                                                isSubmitted && userAnswers[q.id] == option && option != q.correctAnswer -> Color(0xFFFFE0E0)
                                                else -> Color.Transparent
                                            }
                                        )
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = userAnswers[q.id] == option,
                                        onClick = { userAnswers[q.id] = option }
                                    )
                                    Text(option, fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
                                }
                            }
                        } else {
                            OutlinedTextField(
                                value = userAnswers[q.id] ?: "",
                                onValueChange = { userAnswers[q.id] = it },
                                label = { Text("Your answer") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (isSubmitted) {
                                if (userAnswers[q.id]?.trim()?.equals(q.correctAnswer, ignoreCase = true) == true) {
                                    Text("✅ Correct", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                } else {
                                    Text("❌ Correct answer: ${q.correctAnswer}", color = Color.Red, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // ✅ Submit button + Score
            item {
                Button(
                    onClick = { isSubmitted = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit Answers")
                }

                if (isSubmitted) {
                    val score = questions.count {
                        userAnswers[it.id]?.trim()?.equals(it.correctAnswer, ignoreCase = true) == true
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "📊 Your Score: $score / ${questions.size}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF1565C0),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ===== Feedback luôn hiển thị =====
            item {
                Spacer(Modifier.height(16.dp))
                Text("💬 Feedback", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                // Comment input
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    label = { Text("Write your comment...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                feedbackList = feedbackList + Feedback("You", commentText)
                                commentText = ""
                            }
                        }
                    ) {
                        Text("Send")
                    }
                }

                Divider(Modifier.padding(vertical = 12.dp))

                // Feedback list
                feedbackList.forEach { fb ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(fb.user, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Text(fb.comment, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}
