package com.example.relisapp.ui.screens

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
// [SỬA] Không cần import Lessons và HomeViewModel nữa
// import com.example.relisapp.data.local.entity.Lessons
// import com.example.relisapp.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    // [SỬA] Loại bỏ ViewModel và các callback không cần thiết
    // homeViewModel: HomeViewModel,
    onListeningClick: () -> Unit,
    onReadingClick: () -> Unit,
    onProgressClick: () -> Unit,
    onSearchClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    // [SỬA] Loại bỏ tất cả các state thu thập từ ViewModel
    // val filteredLessons by homeViewModel.filteredLessons.collectAsState()
    // val searchText by homeViewModel.searchText.collectAsState()
    // val selectedLevel by homeViewModel.selectedLevel.collectAsState()
    // val selectedType by homeViewModel.selectedType.collectAsState()
    // val isNewest by homeViewModel.isNewestFirst.collectAsState()
    // val favoriteLessons by homeViewModel.favoriteLessons.collectAsState()
    // val currentUser by homeViewModel.currentUser.collectAsState()

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
            // --- 1. WELCOME CARD (HIỂN THỊ DỮ LIỆU TĨNH) ---
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
                                    // [SỬA] Sử dụng tên tĩnh
                                    append("Student")
                                }
                            },
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 32.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // [SỬA] Hiển thị level tĩnh
                        Text(
                            "Current Level: A1",
                            color = Color.White.copy(0.9f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // --- 2. BỘ LỌC (GIỜ CHỈ LÀ GIAO DIỆN TĨNH) ---
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // A. Search Bar (vô hiệu hóa)
                    OutlinedTextField(
                        value = "",
                        onValueChange = {  },
                        enabled = true, // [SỬA] Vô hiệu hóa
                        label = { Text("Search topics...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledContainerColor = Color.White, // [SỬA] Màu khi bị vô hiệu hóa
                            disabledBorderColor = Color.LightGray,
                            disabledLeadingIconColor = Color.Gray,
                            disabledLabelColor = Color.Gray
                        )
                    )

                    // B. Hàng chọn LOẠI BÀI (vô hiệu hóa)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Skill:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val types = listOf("listening", "reading")
                            items(types) { type ->
                                FilterChip(
                                    selected = false,
                                    onClick = {  },
                                    enabled = true, // [SỬA] Vô hiệu hóa
                                    label = {
                                        Text(type.replaceFirstChar { it.uppercase() })
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (type == "listening") Icons.Default.Headphones else Icons.Default.MenuBook,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }

                    // C. Hàng chọn LEVEL & SORT (vô hiệu hóa)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Level:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item {
                                FilterChip(
                                    selected = true,
                                    onClick = {  },
                                    enabled = true, // [SỬA] Vô hiệu hóa
                                    label = { Text("Newest") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.ArrowDownward,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                )
                            }
                            val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")
                            items(levels) { level ->
                                FilterChip(
                                    selected = (level == "A1"), // [SỬA] Chỉ chọn A1 làm mặc định
                                    onClick = {  },
                                    enabled = true, // [SỬA] Vô hiệu hóa
                                    label = { Text(level) },
                                    // [SỬA LỖI Ở ĐÂY]
                                    colors = FilterChipDefaults.filterChipColors(
                                        disabledSelectedContainerColor = Color(0xFF4E8DF5).copy(alpha = 0.5f)
                                        // Bỏ dòng: disabledSelectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // --- 3. HIỂN THỊ DANH SÁCH TĨNH ---

            // [SỬA] Luôn hiển thị giao diện mặc định, loại bỏ isFiltering
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
                    userScrollEnabled = true
                ) {
                    items(
                        listOf(
                            "🎧 Listening" to onListeningClick,
                            "📖 Reading" to onReadingClick,
                            "📊 Progress" to onProgressClick,
                            "❤️ Favorite" to onFavoriteClick
                        )
                    ) { (t, a) -> FeatureButton(t, a) }
                }
            }

            // Recent Lessons (dữ liệu tĩnh)
            item {
                Text("📜 Recent Lessons", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            // [SỬA] Hiển thị các bài học mẫu tĩnh
            item { LessonCardStatic(title = "A Trip to the Zoo", level = "A1", type = "Listening") }
            item { LessonCardStatic(title = "My Daily Routine", level = "A1", type = "Reading") }


            // Recommended (dữ liệu tĩnh)
            item {
                Text("🌟 Recommended", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            // [SỬA] Hiển thị các bài học mẫu tĩnh
            item { LessonCardStatic(title = "At the Restaurant", level = "A2", type = "Listening") }
        }
    }
}

// --- GIỮ NGUYÊN HOẶC SỬA ĐỔI CÁC COMPOSABLE CON ---

// [GIỮ NGUYÊN] FeatureButton không cần thay đổi
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

// [SỬA] Tạo phiên bản tĩnh của LessonCard để không phụ thuộc vào entity 'Lessons'
@Composable
fun LessonCardStatic(
    title: String,
    level: String,
    type: String
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
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFE3F2FD),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = level,
                            fontSize = 10.sp,
                            color = Color(0xFF1976D2),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Surface(
                        color = if (type.equals("listening", true)) Color(0xFFFFF3E0) else Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = type.uppercase(),
                            fontSize = 10.sp,
                            color = if (type.equals("listening", true)) Color(0xFFF57C00) else Color(0xFF388E3C),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {  }, enabled = true) { // [SỬA] Vô hiệu hóa nút favorite
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = Color.Gray
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

// [XÓA] Composable LessonCard cũ không còn cần thiết vì nó phụ thuộc vào entity `Lessons`
/*
@Composable
fun LessonCard(
    lesson: Lessons,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) { ... }
*/
