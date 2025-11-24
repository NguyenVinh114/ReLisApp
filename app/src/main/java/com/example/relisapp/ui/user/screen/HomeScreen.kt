package com.example.relisapp.ui.user.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relisapp.data.local.entity.Lessons
import com.example.relisapp.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onListeningClick: () -> Unit,
    onReadingClick: () -> Unit,
    onProgressClick: () -> Unit,
    onSearchClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    // --- 1. LẤY DATA TỪ VIEWMODEL ---
    val filteredLessons by homeViewModel.filteredLessons.collectAsState()

    // Các biến Filter
    val searchText by homeViewModel.searchText.collectAsState()
    val selectedLevel by homeViewModel.selectedLevel.collectAsState()
    val selectedType by homeViewModel.selectedType.collectAsState() // <--- MỚI: Lấy loại bài
    val isNewest by homeViewModel.isNewestFirst.collectAsState()

    val favoriteLessons by homeViewModel.favoriteLessons.collectAsState()
    val currentUser by homeViewModel.currentUser.collectAsState() // <--- Lấy thông tin User 6

    Scaffold(
        topBar = { TopAppBar(title = { Text("ReLis – English Practice") }) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- 1. WELCOME CARD (HIỂN THỊ TÊN THẬT) ---
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4E8DF5)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color.White)) {
                                    append("👋 Welcome back, \n")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = Color(0xFFFFD54F),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 24.sp
                                    )
                                ) {
                                    // Lấy tên từ User Database, nếu chưa load xong thì hiện "..."
                                    append(currentUser?.username ?: currentUser?.fullName ?: "Student")
                                }
                            },
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 32.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Hiển thị level hiện tại của User lên thẻ
                        Text(
                            "Current Level: ${currentUser?.level ?: "Unknown"}",
                            color = Color.White.copy(0.9f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // --- 2. BỘ LỌC (SEARCH + TYPE + LEVEL) ---
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // A. Search Bar
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { homeViewModel.onSearchTextChange(it) },
                        label = { Text("Search topics...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    // B. Hàng chọn LOẠI BÀI (Skill) - MỚI THÊM
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Skill:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val types = listOf("listening", "reading") // Khớp với DB
                            items(types) { type ->
                                FilterChip(
                                    selected = selectedType == type,
                                    onClick = { homeViewModel.onTypeSelected(type) },
                                    label = {
                                        // Viết hoa chữ cái đầu (reading -> Reading)
                                        Text(type.replaceFirstChar { it.uppercase() })
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (type == "listening") Icons.Default.Headphones else Icons.Default.MenuBook,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFFFD54F), // Màu vàng cho Skill
                                        selectedLabelColor = Color.Black
                                    )
                                )
                            }
                        }
                    }

                    // C. Hàng chọn LEVEL & SORT
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Level:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Nút Sort Time
                            item {
                                FilterChip(
                                    selected = isNewest,
                                    onClick = { homeViewModel.onSortToggle() },
                                    label = { Text(if (isNewest) "Newest" else "Oldest") },
                                    leadingIcon = {
                                        Icon(
                                            if (isNewest) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                )
                            }

                            // Các nút Level
                            val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")
                            items(levels) { level ->
                                FilterChip(
                                    selected = selectedLevel == level, // Tự động sáng nếu User có level này
                                    onClick = { homeViewModel.onLevelSelected(level) },
                                    label = { Text(level) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF4E8DF5), // Màu xanh cho Level
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // --- 3. HIỂN THỊ DANH SÁCH ---

            // Logic: Nếu có bất kỳ bộ lọc nào (Search, Type, Level) -> Hiện kết quả lọc
            val isFiltering = searchText.isNotEmpty() || selectedLevel != null || selectedType != null

            if (isFiltering) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "🔍 Results (${filteredLessons.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        if (filteredLessons.isEmpty()) {
                            Text(
                                " (Try changing filters)",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
                items(filteredLessons) { lesson ->
                    val isFav = favoriteLessons.any { it.lessonId == lesson.lessonId }
                    LessonCard(lesson, isFav) {
                        homeViewModel.toggleFavorite(lesson.lessonId)
                    }
                }
            } else {
                // --- GIAO DIỆN MẶC ĐỊNH (Khi chưa chọn gì) ---

                // Main Features Grid
                item {
                    Text("📂 Main Features", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        userScrollEnabled = false
                    ) {
                        items(
                            listOf(
                                // --- SỬA Ở ĐÂY ---
                                // Cũ (Sai): { homeViewModel.onTypeSelected("listening") }
                                // Mới (Đúng): gọi onListeningClick để chuyển Activity
                                "🎧 Listening" to onListeningClick,

                                // Cũ (Sai): { homeViewModel.onTypeSelected("reading") }
                                // Mới (Đúng): gọi onReadingClick để chuyển Activity
                                "📖 Reading" to onReadingClick,
                                // ------------------

                                "📊 Progress" to onProgressClick,
                                // "🔍 Search" to onSearchClick, // (Nếu bạn bỏ comment thì dùng cái này)

                                "❤️ Favorite" to onFavoriteClick
                            )
                        ) { (t, a) -> FeatureButton(t, a) }
                    }
                }

                // Recent Lessons (Lấy 3 bài đầu)
                item {
                    Text("📜 Recent Lessons", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                items(filteredLessons.take(3)) { lesson ->
                    val isFav = favoriteLessons.any { it.lessonId == lesson.lessonId }
                    LessonCard(lesson, isFav) {
                        homeViewModel.toggleFavorite(lesson.lessonId)
                    }
                }

                // Recommended (Lấy 3 bài cuối)
                item {
                    Text("🌟 Recommended", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                items(filteredLessons.takeLast(3)) { lesson ->
                    val isFav = favoriteLessons.any { it.lessonId == lesson.lessonId }
                    LessonCard(lesson, isFav) {
                        homeViewModel.toggleFavorite(lesson.lessonId)
                    }
                }
            }
        }
    }
}

// --- GIỮ NGUYÊN CÁC COMPOSABLE CON ---
@Composable
fun FeatureButton(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LessonCard(
    lesson: Lessons,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(lesson.title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Badge cho Level
                    Surface(
                        color = Color(0xFFE3F2FD),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = lesson.level ?: "N/A",
                            fontSize = 10.sp,
                            color = Color(0xFF1976D2),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    // Badge cho Type
                    Surface(
                        color = if (lesson.type.equals("listening", true)) Color(0xFFFFF3E0) else Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = lesson.type.uppercase(),
                            fontSize = 10.sp,
                            color = if (lesson.type.equals("listening", true)) Color(0xFFF57C00) else Color(0xFF388E3C),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.Gray
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        }
    }
}