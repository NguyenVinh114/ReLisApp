@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.relisapp.ui.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Feedback(
    val user: String,
    val comment: String
)

@Composable
fun FeedbackScreen(onBack: () -> Unit, lessonTitle: String = "Listening – Travel") {
    var isLiked by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }

    // ✅ Data giả (sau này lấy từ DB)
    var feedbackList by remember {
        mutableStateOf(
            listOf(
                Feedback("Alice", "Great lesson, very helpful!"),
                Feedback("Bob", "I liked the transcript feature.")
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("💬 Feedback – $lessonTitle") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF9F9F9))
        ) {
            // ❤️ Like button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = { isLiked = !isLiked }) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Like",
                        tint = if (isLiked) Color.Red else Color.Gray
                    )
                }
                Text(
                    if (isLiked) "You liked this lesson" else "Like this lesson?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // 📝 Comment input
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                label = { Text("Write your comment...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Button(
                onClick = {
                    if (commentText.isNotBlank()) {
                        feedbackList = feedbackList + Feedback("You", commentText)
                        commentText = ""
                    }
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(16.dp)
            ) {
                Text("Send")
            }

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            // 💬 Hiển thị comment list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(feedbackList) { fb ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
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
