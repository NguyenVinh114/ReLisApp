@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.relisapp.ui.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relisapp.data.local.entity.Lessons // Import Entity Lessons
import com.example.relisapp.ui.viewmodel.HomeViewModel
import com.example.relisapp.ui.user.screen.LessonCard

@Composable
fun FavoriteScreen(
    homeViewModel: HomeViewModel,
    onBack: () -> Unit,
    // 1. THÊM THAM SỐ CALLBACK NÀY
    onLessonClick: (Lessons) -> Unit
) {
    val favoriteLessons by homeViewModel.favoriteLessonsDetail.collectAsState()

    var isShuffle by remember { mutableStateOf(false) }
    val displayList = if (isShuffle) favoriteLessons.shuffled() else favoriteLessons

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("❤️ Favorite Lessons") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text("Shuffle", fontSize = 14.sp)
                        Switch(
                            checked = isShuffle,
                            onCheckedChange = { isShuffle = it }
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (displayList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF9F9F9)),
                contentAlignment = Alignment.Center
            ) {
                Text("No favorites yet", fontSize = 18.sp, color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(Color(0xFFF9F9F9)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayList) { lesson ->
                    // 2. BỌC LESSON CARD TRONG BOX ĐỂ BẮT SỰ KIỆN CLICK
                    Box(
                        modifier = Modifier.clickable {
                            onLessonClick(lesson) // Gọi hàm khi click vào bài
                        }
                    ) {
                        LessonCard(
                            lesson = lesson,
                            isFavorite = true,
                            onToggleFavorite = { homeViewModel.toggleFavorite(lesson.lessonId) }
                        )
                    }
                }
            }
        }
    }
}