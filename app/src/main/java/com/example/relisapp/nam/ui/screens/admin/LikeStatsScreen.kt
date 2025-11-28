package com.example.relisapp.nam.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.relisapp.nam.viewmodel.LikeStatsUiState
import com.example.relisapp.nam.viewmodel.LikeStatsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikeStatsScreen(vm: LikeStatsViewModel) {
    val uiState by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thống Kê Tương Tác") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is LikeStatsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is LikeStatsUiState.Error -> {
                    Text(
                        text = "Lỗi: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is LikeStatsUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Card 1: Tổng
                        item { TotalLikesCard(state.totalLikes) }

                        // Card 2: Top Bài
                        item { TopLessonsCard(state.topLessons) }

                        // Card 3: Top User
                        item { TopUsersCard(state.topUsers) }

                        // Card 4: Recent
                        item { RecentLikesCard(state.recentLikes) }
                    }
                }
            }
        }
    }
}