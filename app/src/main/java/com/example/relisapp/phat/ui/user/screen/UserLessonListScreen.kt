package com.example.relisapp.phat.ui.user.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.relisapp.phat.entity.Lessons
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserLessonListScreen(
    lessons: List<Lessons>,
    modifier: Modifier = Modifier,
    onLessonClick: (Lessons) -> Unit
) {
    // --- STATE CHO BỘ LỌC VÀ TÌM KIẾM ---
    var selectedLevel by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    val levels = listOf("All", "A1", "A2", "B1", "B2", "C1", "C2")

    // --- DEBOUNCING CHO TÌM KIẾM ĐỂ TỐI ƯU HIỆU NĂNG ---
    var debouncedSearchQuery by remember { mutableStateOf(searchQuery) }
    LaunchedEffect(searchQuery) {
        delay(300L)
        debouncedSearchQuery = searchQuery
    }

    // --- KẾT HỢP LOGIC LỌC VÀ TÌM KIẾM ---
    val filteredLessons = remember(debouncedSearchQuery, selectedLevel, lessons) {
        lessons.filter { lesson ->
            val levelMatch = selectedLevel == "All" || lesson.level.equals(selectedLevel, ignoreCase = true)
            val searchMatch = lesson.title.contains(debouncedSearchQuery, ignoreCase = true)
            levelMatch && searchMatch
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // --- GIAO DIỆN THANH TÌM KIẾM VÀ LỌC ---
        SearchBarAndFilter(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            levels = levels,
            selectedLevel = selectedLevel,
            onLevelSelected = { selectedLevel = it },
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
        )

        // --- GIAO DIỆN DANH SÁCH ---
        if (filteredLessons.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (lessons.isEmpty()) "No lessons in this category." else "No lessons match your search or filter.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredLessons, key = { it.lessonId }) { lesson ->
                    LessonCard(lesson = lesson, onClick = { onLessonClick(lesson) })
                }
            }
        }
    }
}

// [CẬP NHẬT] GOM TÌM KIẾM VÀ LỌC VÀO CÙNG MỘT HÀNG
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarAndFilter(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    levels: List<String>,
    selectedLevel: String,
    onLevelSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var levelFilterExpanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- THANH TÌM KIẾM ---
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.weight(1f), // Chiếm phần lớn không gian
            label = { Text("Search...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            singleLine = true
        )

        // --- BỘ LỌC CẤP ĐỘ (SỬ DỤNG BUTTON) ---
        ExposedDropdownMenuBox(
            expanded = levelFilterExpanded,
            onExpandedChange = { levelFilterExpanded = !it }
        ) {
            // [SỬA] Thay IconButton bằng OutlinedButton để hiển thị cả chữ và icon
            OutlinedButton(
                onClick = { levelFilterExpanded = true },
                shape = MaterialTheme.shapes.medium,
                // Button sẽ tự động điều chỉnh độ rộng để vừa với nội dung bên trong
                modifier = Modifier.menuAnchor()
            ) {
                Icon(
                    Icons.Default.FilterList,
                    contentDescription = "Filter by level",
                    modifier = Modifier.size(18.dp) // Kích thước icon nhỏ gọn
                )
                Spacer(Modifier.width(8.dp))
                // Hiển thị cấp độ đang được chọn
                Text(text = selectedLevel)
            }

            ExposedDropdownMenu(
                expanded = levelFilterExpanded,
                onDismissRequest = { levelFilterExpanded = false }
            ) {
                // Thêm tiêu đề cho menu
                DropdownMenuItem(
                    text = { Text("Filter by Level", fontWeight = FontWeight.Bold) },
                    onClick = {},
                    enabled = false
                )
                HorizontalDivider()
                levels.forEach { level ->
                    DropdownMenuItem(
                        text = { Text(level) },
                        onClick = {
                            onLevelSelected(level)
                            levelFilterExpanded = false
                        }
                    )
                }
            }
        }
    }
}

// [CẬP NHẬT] TRỞ VỀ ĐỊNH DẠNG BAN ĐẦU CỦA LESSON CARD
@Composable
private fun LessonCard(
    lesson: Lessons,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Level: ${lesson.level ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Start lesson",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
