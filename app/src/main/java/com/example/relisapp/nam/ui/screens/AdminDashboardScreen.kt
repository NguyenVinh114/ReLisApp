package com.example.relisapp.nam.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.relisapp.nam.viewmodel.CommentModerationViewModel
import com.example.relisapp.nam.viewmodel.LikeStatsUiState
import com.example.relisapp.nam.viewmodel.LikeStatsViewModel
import com.example.relisapp.nam.viewmodel.ModerationUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    commentVM: CommentModerationViewModel,
    likeStatsVM: LikeStatsViewModel,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Kiểm duyệt Bình luận", "Thống kê Likes")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Comment And Like") },
                navigationIcon = {
                    IconButton(onClick = onBack) {     // ⭐ NÚT QUAY LẠI
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // --- TAB ROW ---
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            // --- CONTENT ---
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                when (selectedTab) {
                    0 -> CommentTabContent(commentVM) // Tab cũ
                    1 -> LikeStatsTabContent(likeStatsVM) // Tab mới
                }
            }
        }
    }
}

// ==========================================
// 👇 TAB 1: NỘI DUNG BÌNH LUẬN (Code cũ)
// ==========================================
@Composable
fun CommentTabContent(vm: CommentModerationViewModel) {
    val uiState by vm.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var search by remember { mutableStateOf("") }

    Column {
        // Search Bar nhỏ gọn
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Tìm bình luận...") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                if (search.isNotEmpty()) IconButton(onClick = { search = "" }) {
                    Icon(Icons.Default.Clear, null)
                }
            }
        )
        Spacer(Modifier.height(12.dp))

        when (val state = uiState) {
            is ModerationUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is ModerationUiState.Error -> Text("Lỗi: ${state.message}", color = Color.Red)
            is ModerationUiState.Success -> {
                val filtered = state.comments.filter { it.comment.content.contains(search, true) }

                if (filtered.isEmpty()) {
                    Text("Không có dữ liệu.", fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(filtered) { item ->
                            // Gọi lại Card cũ của bạn
                            ModerationCommentCardEnhanced(
                                commentWithDetails = item,
                                onDelete = { scope.launch { vm.deleteComment(item.comment) } }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 👇 TAB 2: NỘI DUNG THỐNG KÊ LIKE (Code mới)
// ==========================================
@Composable
fun LikeStatsTabContent(vm: LikeStatsViewModel) {
    val uiState by vm.uiState.collectAsState()

    when (val state = uiState) {
        is LikeStatsUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        is LikeStatsUiState.Error -> Text("Lỗi: ${state.message}", color = Color.Red)
        is LikeStatsUiState.Success -> {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Gọi lại các Card Dashboard mới làm
                item { TotalLikesCard(state.totalLikes) }
                item { TopLessonsCard(state.topLessons) }
                item { TopUsersCard(state.topUsers) }
                item { RecentLikesCard(state.recentLikes) }
            }
        }
    }
}