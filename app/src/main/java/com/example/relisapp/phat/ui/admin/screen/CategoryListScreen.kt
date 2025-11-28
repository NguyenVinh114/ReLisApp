package com.example.relisapp.phat.ui.admin.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.relisapp.phat.entity.Categories
import com.example.relisapp.phat.viewmodel.CategoryViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    categories: List<Categories>,
    viewModel: CategoryViewModel,
    onAddClick: () -> Unit,
    onEditClick: (Categories) -> Unit,
    onLockSuccess: () -> Unit, // Callback này giờ sẽ được gọi sau khi khóa/mở khóa
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var typeExpanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("All") }
    val types = listOf("All", "Listening", "Reading")
    val focusManager = LocalFocusManager.current

    var debouncedSearchQuery by remember { mutableStateOf(searchQuery) }
    LaunchedEffect(searchQuery) {
        delay(300L)
        debouncedSearchQuery = searchQuery
    }

    // Lọc danh sách dựa trên bộ lọc và tìm kiếm, không còn lọc theo isLocked
    val filteredCategories = categories.filter { category ->
        val selectedTypeLower = selectedType.lowercase()
        (selectedType == "All" || category.type.equals(selectedTypeLower, ignoreCase = true)) &&
                category.categoryName.contains(debouncedSearchQuery, ignoreCase = true)
    }

    // State để quản lý dialog xác nhận khóa hoặc mở khóa
    var categoryToToggleLock by remember { mutableStateOf<Categories?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Filled.Add, contentDescription = "Create New Category")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hàng chứa bộ lọc và tìm kiếm
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search") },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.Search, "Search Icon") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, "Clear Search")
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    singleLine = true
                )

                FilterTypeDropdown(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it },
                    selectedValue = selectedType,
                    onValueSelected = {
                        selectedType = it
                        typeExpanded = false
                    },
                    options = types
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (filteredCategories.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredCategories, key = { it.categoryId }) { category ->
                            CategoryItem(
                                category = category,
                                onEdit = { onEditClick(category) },
                                onToggleLock = { categoryToToggleLock = category }
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (debouncedSearchQuery.isNotEmpty() || selectedType != "All") "No categories match the filter" else "No categories available. Add one!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    // Dialog xác nhận khóa/mở khóa
    categoryToToggleLock?.let { category ->
        val isLocking = category.isLocked == 0
        val actionText = if (isLocking) "Lock" else "Unlock"
        val dialogTitle = "Confirm $actionText"
        val dialogText = "Are you sure you want to $actionText the category \"${category.categoryName}\"?"

        AlertDialog(
            onDismissRequest = { categoryToToggleLock = null },
            title = { Text(dialogTitle) },
            text = { Text(dialogText) },
            confirmButton = {
                Button(
                    onClick = {
                        if (isLocking) {
                            viewModel.lockCategory(category)
                        } else {
                            viewModel.unLockCategory(category)
                        }
                        onLockSuccess() // Hiển thị Toast thông báo thành công
                        categoryToToggleLock = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLocking) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(actionText)
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToToggleLock = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun CategoryItem(
    category: Categories,
    onEdit: () -> Unit,
    onToggleLock: () -> Unit
) {
    val itemIcon = when (category.type.lowercase()) {
        "listening" -> Icons.Default.Headset
        "reading" -> Icons.Default.MenuBook
        else -> Icons.Default.Category
    }

    // Xác định màu sắc và độ trong suốt dựa trên trạng thái isLocked
    val containerColor = if (category.isLocked == 1) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
    } else {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = if (category.isLocked == 1) {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = category.categoryName,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
            },
            supportingContent = {
                Text(
                    text = "Type: ${category.type.replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.8f)
                )
            },
            leadingContent = {
                Icon(
                    imageVector = itemIcon,
                    contentDescription = null,
                    tint = if (category.isLocked == 1) contentColor.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            },
            trailingContent = {
                Row {
                    // Nút Sửa
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Category",
                            tint = contentColor.copy(alpha = 0.8f)
                        )
                    }

                    // Nút Khóa / Mở khóa
                    IconButton(onClick = onToggleLock) {
                        if (category.isLocked == 0) {
                            Icon(
                                imageVector = Icons.Default.LockOpen,
                                contentDescription = "Lock Category",
                                tint = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Unlock Category",
                                tint = contentColor.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            },
            // Không cần màu riêng cho ListItem vì đã set màu cho Card
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterTypeDropdown(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    selectedValue: String,
    onValueSelected: (String) -> Unit,
    options: List<String>,
    modifier: Modifier = Modifier
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text("Type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .width(145.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            options.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type) },
                    onClick = { onValueSelected(type) }
                )
            }
        }
    }
}
