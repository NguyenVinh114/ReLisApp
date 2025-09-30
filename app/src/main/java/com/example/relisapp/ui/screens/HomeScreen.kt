@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.relisapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relisapp.ui.favorite.FavoriteManager

@Composable
fun HomeScreen(
    onListeningClick: () -> Unit,
    onReadingClick: () -> Unit,
    onProgressClick: () -> Unit,
    onSearchClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    val recentLessons = remember {
        listOf("Listening: Travel", "Reading: Environment", "Listening Test A1")
    }
    val recommendedLessons = remember {
        listOf("New Listening - Daily Life", "Reading - Health", "Listening - Music")
    }

    var searchText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("ReLis – English Practice") })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 👋 Welcome banner
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4E8DF5)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "👋 Welcome back,",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Ready to improve your Listening & Reading skills?",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            // 🔍 Search bar
            item {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Search lessons...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // 📚 Main Features (Grid)
            item {
                Text("📂 Main Features", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),   // 3 cột
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)            // đặt cao để
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false
                ) {
                    items(listOf(
                        "🎧 Listening" to onListeningClick,
                        "📖 Reading" to onReadingClick,
                        "📊 Progress" to onProgressClick,
                        "🔍 Search" to onSearchClick,
                        "❤️ Favorite" to onFavoriteClick
                    )) { (title, action) ->
                        FeatureButton(title, action)
                    }
                }
            }

            // 📜 Recent Lessons
            item {
                Text("📜 Recent Lessons", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            items(recentLessons) { lesson ->
                LessonCard(title = lesson)
            }

            // 🌟 Recommended Lessons
            item {
                Text("🌟 Recommended Lessons", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            items(recommendedLessons) { lesson ->
                LessonCard(title = lesson)
            }
        }
    }
}

@Composable
fun FeatureButton(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // vuông đẹp
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    }
}
@Composable
fun LessonCard(title: String) {
    var isFavorite by remember { mutableStateOf(FavoriteManager.favorites.contains(title)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontSize = 16.sp)

            Row(verticalAlignment = Alignment.CenterVertically) {
                // ❤️ Favorite button
                IconButton(onClick = {
                    if (isFavorite) {
                        FavoriteManager.removeFavorite(title)
                    } else {
                        FavoriteManager.addFavorite(title)
                    }
                    isFavorite = !isFavorite
                }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.Gray
                    )
                }

                Text("→", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

