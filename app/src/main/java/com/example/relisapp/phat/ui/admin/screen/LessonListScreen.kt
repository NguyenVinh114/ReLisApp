package com.example.relisapp.phat.ui.admin.screen

import androidx.compose.animation.core.copy
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.relisapp.phat.entity.Categories
import com.example.relisapp.phat.entity.Lessons
import com.example.relisapp.phat.viewmodel.LessonViewModel

// ... (Các import giữ nguyên)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonListScreen(
    modifier: Modifier = Modifier,
    lessons: List<Lessons>,
    categories: List<Categories>,
    viewModel: LessonViewModel,
    onAddLesson: () -> Unit,
    onEditClick: (Int) -> Unit,
    onSearch: (String) -> Unit,
    onFilter: (type: String?, categoryId: Int?) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }

    // [CẬP NHẬT] State để quản lý dialog xác nhận khóa/mở khóa
    var lessonToToggleLock by remember { mutableStateOf<Lessons?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddLesson,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Lesson")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Thanh Tìm kiếm và Lọc
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        onSearch(it)
                    },
                    label = { Text("Search by title...") },
                    leadingIcon = { Icon(Icons.Default.Search, "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = ""; onSearch("") }) {
                                Icon(Icons.Default.Clear, "Clear")
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { showFilterDialog = true }) {
                    Icon(Icons.Default.FilterList, "Filter")
                }
            }

            if (showFilterDialog) {
                FilterDialog(
                    categories = categories,
                    onDismiss = { showFilterDialog = false },
                    onApplyFilter = { type, categoryId ->
                        onFilter(type, categoryId)
                        showFilterDialog = false
                    }
                )
            }

            // Danh sách Bài học
            if (lessons.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No lessons found.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(lessons, key = { it.lessonId }) { lesson ->
                        LessonItemCard(
                            lesson = lesson,
                            categoryName = categories.find { it.categoryId == lesson.categoryId }?.categoryName ?: "N/A",
                            // [CẬP NHẬT] Truyền các callback mới
                            onEdit = { onEditClick(lesson.lessonId) },
                            onToggleLock = { lessonToToggleLock = lesson }
                        )
                    }
                }
            }
        }
    }

    // [CẬP NHẬT] Dialog xác nhận khóa/mở khóa
    lessonToToggleLock?.let { lesson ->
        val isLocking = lesson.isLocked == 0
        val actionText = if (isLocking) "Lock" else "Unlock"
        AlertDialog(
            onDismissRequest = { lessonToToggleLock = null },
            title = { Text("Confirm $actionText") },
            text = { Text("Are you sure you want to $actionText the lesson \"${lesson.title}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        if (isLocking) viewModel.lockLesson(lesson) else viewModel.unlockLesson(lesson)
                        lessonToToggleLock = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLocking) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(actionText)
                }
            },
            dismissButton = {
                TextButton(onClick = { lessonToToggleLock = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
fun LessonItemCard(
    lesson: Lessons,
    categoryName: String,
    // [CẬP NHẬT] Nhận 2 callback riêng biệt
    onEdit: () -> Unit,
    onToggleLock: () -> Unit
) {
    // Xác định màu sắc dựa trên trạng thái isLocked
    val cardAlpha = if (lesson.isLocked == 1) 0.6f else 1f
    val contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = cardAlpha)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit), // Vẫn giữ click cả card để sửa
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = cardAlpha)
        )
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Category: $categoryName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = cardAlpha)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    val typeColor = getColorForLessonType(type = lesson.type)
                    Chip(label = lesson.type, color = typeColor.copy(alpha = cardAlpha))
                    Spacer(modifier = Modifier.width(8.dp))
                    lesson.level?.let {
                        Chip(label = it, color = MaterialTheme.colorScheme.tertiary.copy(alpha = cardAlpha))
                    }
                }
            }
            // [CẬP NHẬT] Hàng chứa các icon thao tác
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Lesson",
                        tint = contentColor
                    )
                }
                IconButton(onClick = onToggleLock) {
                    if (lesson.isLocked == 0) {
                        Icon(
                            Icons.Default.LockOpen,
                            contentDescription = "Lock Lesson",
                            tint = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Unlock Lesson",
                            tint = contentColor
                        )
                    }
                }
            }
        }
    }
}
// CẬP NHẬT TRONG COMPOSABLE NÀY
@Composable
private fun Chip(label: String, color: Color) { // Thêm tham số `color`
    Surface(
        shape = MaterialTheme.shapes.medium,
        // 3. SỬ DỤNG MÀU ĐƯỢC TRUYỀN VÀO
        color = color.copy(alpha = 0.15f), // Nền là màu chính nhưng rất nhạt
        border = null
    ) {
        Text(
            text = label.replaceFirstChar { it.titlecase() },
            style = MaterialTheme.typography.labelMedium,
            // 4. MÀU CHỮ LÀ MÀU CHÍNH ĐỂ ĐẢM BẢO ĐỘ TƯƠNG PHẢN
            color = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

// 5. HÀM HELPER ĐỂ LẤY MÀU CHO TỪNG LOẠI
@Composable
private fun getColorForLessonType(type: String): Color {
    // Chúng ta sẽ định nghĩa các màu riêng để chúng không bị ảnh hưởng bởi theme
    val listeningColor = Color(0xFF4A90E2) // Màu Xanh dương
    val readingColor = Color(0xFF50E3C2)   // Màu Xanh ngọc
    val grammarColor = Color(0xFFF5A623)   // Màu Vàng cam

    // Dùng when để trả về màu tương ứng
    return when (type.lowercase()) {
        "listening" -> listeningColor
        "reading" -> readingColor
        "grammar" -> grammarColor
        else -> MaterialTheme.colorScheme.onSurfaceVariant // Màu mặc định nếu có loại mới
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    categories: List<Categories>,
    onDismiss: () -> Unit,
    onApplyFilter: (type: String?, categoryId: Int?) -> Unit
) {
    var selectedType by remember { mutableStateOf<String?>(null) }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }

    val filteredCategories = remember(selectedType, categories) {
        categories.filter { it.type == selectedType }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Lessons") },
        text = {
            Column {
                // Dropdown cho Type
                var typeExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = !typeExpanded }) {
                    OutlinedTextField(
                        value = selectedType ?: "All Types",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type") },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                        DropdownMenuItem(text = { Text("All Types") }, onClick = { selectedType = null; typeExpanded = false })
                        listOf("Listening", "Reading").forEach { type ->
                            val lower = type.lowercase()
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedType = lower
                                    selectedCategoryId = null
                                    typeExpanded = false
                                }
                            )
                        }

                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dropdown cho Category
                var catExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = catExpanded, onExpandedChange = { catExpanded = !catExpanded }) {
                    OutlinedTextField(
                        value = categories.find { it.categoryId == selectedCategoryId }?.categoryName ?: "All Categories",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        modifier = Modifier.menuAnchor(),
                        enabled = selectedType != null
                    )
                    ExposedDropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                        DropdownMenuItem(text = { Text("All Categories") }, onClick = { selectedCategoryId = null; catExpanded = false })
                        filteredCategories.forEach { category ->
                            DropdownMenuItem(text = { Text(category.categoryName) }, onClick = { selectedCategoryId = category.categoryId; catExpanded = false })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onApplyFilter(selectedType, selectedCategoryId) }) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
