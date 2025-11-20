package com.example.relisapp.phat.ui.admin.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.relisapp.phat.entity.Categories

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    categories: List<Categories>,
    onAddClick: () -> Unit,
    // [QUAN TRỌNG] Nhận modifier từ Activity (chứa padding của BaseScreen)
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var typeExpanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("All") }
    val types = listOf("All", "Listening", "Reading")

    // Logic lọc danh sách
    val filteredCategories = categories.filter { category ->
        val selectedTypeLower = selectedType.lowercase()
        (selectedType == "All" || category.type == selectedTypeLower) &&
                category.categoryName.contains(searchQuery, ignoreCase = true)
    }

    // Áp dụng modifier được truyền vào để set padding tổng
    Column(
        modifier = modifier
            .fillMaxSize()
        // Lưu ý: BaseAdminScreen đã padding nội dung rồi,
        // nhưng ta có thể thêm padding nhỏ ở đây nếu muốn cách lề thêm.
    ) {
        // Nút Create New
        Button(
            onClick = onAddClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Create New")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ô tìm kiếm
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search by name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { /* Ẩn bàn phím */ })
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown lọc loại (Filter Type)
        ExposedDropdownMenuBox(
            expanded = typeExpanded,
            onExpandedChange = { typeExpanded = !typeExpanded }
        ) {
            OutlinedTextField(
                value = selectedType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Filter by Type") },
                trailingIcon = {
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = typeExpanded,
                onDismissRequest = { typeExpanded = false }
            ) {
                types.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            selectedType = type
                            typeExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Danh sách hiển thị
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredCategories) { category ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(category.categoryName)
                        Text(category.type, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            if (filteredCategories.isEmpty()) {
                item {
                    Text("No categories found", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}