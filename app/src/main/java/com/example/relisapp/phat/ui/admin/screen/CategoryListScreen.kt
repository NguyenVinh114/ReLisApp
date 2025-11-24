// package com.example.relisapp.phat.ui.admin.screen
package com.example.relisapp.phat.ui.admin.screen

import androidx.compose.foundation.clickable
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
    // [CẬP NHẬT] Nhận ViewModel để thực hiện xóa
    viewModel: CategoryViewModel,
    onAddClick: () -> Unit,
    // [CẬP NHẬT] Thêm hàm callback cho sự kiện sửa
    onEditClick: (Categories) -> Unit,
    onDeleteSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var typeExpanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("All") }
    val types = listOf("All", "Listening", "Reading") // Thêm Grammar
    val focusManager = LocalFocusManager.current

    var debouncedSearchQuery by remember { mutableStateOf(searchQuery) }
    LaunchedEffect(searchQuery) {
        delay(300L)
        debouncedSearchQuery = searchQuery
    }

    val filteredCategories = categories.filter { category ->
        val selectedTypeLower = selectedType.lowercase()
        (selectedType == "All" || category.type.equals(selectedTypeLower, ignoreCase = true)) &&
                category.categoryName.contains(debouncedSearchQuery, ignoreCase = true)
    }

    // [CẬP NHẬT] State để quản lý dialog xác nhận xóa
    var categoryToDelete by remember { mutableStateOf<Categories?>(null) }

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
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp), // Thêm padding bottom
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredCategories, key = { it.categoryId }) { category ->
                            CategoryItem(
                                category = category,
                                onEdit = { onEditClick(category) },
                                // [CẬP NHẬT] Khi nhấn xóa, hiển thị dialog xác nhận
                                onDelete = { categoryToDelete = category }
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

    // [MỚI] Dialog xác nhận xóa
    if (categoryToDelete != null) {
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete the category \"${categoryToDelete!!.categoryName}\"? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCategory(categoryToDelete!!)
                        onDeleteSuccess()
                        categoryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun CategoryItem(
    category: Categories,
    // [CẬP NHẬT] Thêm 2 callback cho sự kiện Sửa và Xóa
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    // [CẬP NHẬT] State để quản lý menu dropdown
    var menuExpanded by remember { mutableStateOf(false) }

    val itemIcon = when (category.type.lowercase()) {
        "listening" -> Icons.Default.Headset
        "reading" -> Icons.Default.MenuBook
        else -> Icons.Default.Category
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        // [CẬP NHẬT] Thêm sự kiện click vào Card để Sửa
        onClick = onEdit
    ) {
        ListItem(
            headlineContent = { Text(category.categoryName, fontWeight = FontWeight.SemiBold) },
            supportingContent = {
                Text(
                    text = "Type: ${category.type.replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingContent = {
                Icon(
                    imageVector = itemIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            },
            trailingContent = {
                // [CẬP NHẬT] Box chứa nút "More" để mở menu
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options")
                    }
                    // Menu cho Sửa và Xóa
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                onEdit()
                                menuExpanded = false
                            },
                            leadingIcon = { Icon(Icons.Default.Edit, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                onDelete()
                                menuExpanded = false
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
                        )
                    }
                }
            },
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
        )
    }
}

// Composable FilterTypeDropdown giữ nguyên như bạn đã cung cấp
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
            // [CẬP NHẬT] Sử dụng .width() để có chiều rộng CỐ ĐỊNH
            modifier = Modifier
                .menuAnchor()
                .width(145.dp) // <-- Đặt chiều rộng cố định ở đây
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
            // Không cần offset nữa
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