@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.relisapp.nam.ui.screens.home
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.relisapp.R

@Composable
fun HomeScreen(
    onListeningClick: () -> Unit,
    onReadingClick: () -> Unit,
    onProgressClick: () -> Unit,
    onSearchClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ReLis – English Practice") },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            painterResource(id = R.drawable.outline_article_person_24),
                            contentDescription = "Profile"
                        )
                    }
                }
            )
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

            // Welcome banner
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4E8DF5)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("👋 Welcome back!", color = Color.White, fontSize = 20.sp)
                        Text("Choose a skill to practice today.", color = Color.White, fontSize = 15.sp)
                    }
                }
            }

            // Search bar
            item {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Search lessons...") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Feature grid
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false
                ) {
                    items(
                        listOf(
                            "🎧 Listening" to onListeningClick,
                            "📖 Reading" to onReadingClick,
                            "📊 Progress" to onProgressClick,
                            "🔍 Search" to onSearchClick,
                            "❤️ Favorite" to onFavoriteClick
                        )
                    ) { (title, action) ->
                        FeatureCard(title, action)
                    }
                }
            }
        }
    }
}


@Composable
fun FeatureCard(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(text, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen(
        onListeningClick = {},
        onReadingClick = {},
        onProgressClick = {},
        onSearchClick = {},
        onFavoriteClick = {},
        onProfileClick = {}
    )
}
