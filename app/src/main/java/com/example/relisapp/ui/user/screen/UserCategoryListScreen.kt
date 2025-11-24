package com.example.relisapp.ui.user.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.relisapp.data.local.entity.Categories

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCategoryListScreen(
    categories: List<Categories>,
    onCategoryClick: (Categories) -> Unit,
    modifier: Modifier = Modifier,
    fromMain: String
) {
    var searchQuery by remember { mutableStateOf("") }
    var typeExpanded by remember { mutableStateOf(false) }
    var selectedType by remember {
        mutableStateOf(
            when {
                fromMain.equals("Listening", ignoreCase = true) -> "Listening"
                fromMain.equals("Reading", ignoreCase = true) -> "Reading"
                else -> "All"
            }
        )
    }

    val types = listOf("All", "Listening", "Reading")
    val focusManager = LocalFocusManager.current

    // Logic lọc
    val filteredCategories = categories.filter { category ->
        val matchType = selectedType == "All" || category.type.equals(selectedType, ignoreCase = true)
        val matchName = category.categoryName.contains(searchQuery, ignoreCase = true)
        matchType && matchName
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // --- 1. SEARCH BAR (STYLE: CAPSULE & SHADOW) ---
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search topics...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, shape = MaterialTheme.shapes.extraLarge), // Đổ bóng
            shape = MaterialTheme.shapes.extraLarge, // Bo tròn kiểu viên thuốc
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.Transparent, // Ẩn viền
                unfocusedBorderColor = Color.Transparent
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- 2. FILTER DROPDOWN ---
        ExposedDropdownMenuBox(
            expanded = typeExpanded,
            onExpandedChange = { typeExpanded = !typeExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedType,           // giá trị đã gán từ fromMain
                onValueChange = {},             // không cho chỉnh sửa
                readOnly = true,                // khóa edit
                label = { Text("Filter by Type") },
                trailingIcon = null,            // ẩn mũi tên dropdown
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth(), // không cần menuAnchor nữa
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            ExposedDropdownMenu(
                expanded = typeExpanded,
                onDismissRequest = { typeExpanded = false },
                modifier = Modifier.background(Color.White)
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

        // --- 3. LIST RESULT ---
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filteredCategories) { category ->
                // Logic màu cho từng loại
                val isListening = category.type.equals("Listening", ignoreCase = true)
                val tagBgColor = if (isListening) Color(0xFFE1F5FE) else Color(0xFFFFF3E0) // Xanh nhạt / Cam nhạt
                val tagTextColor = if (isListening) Color(0xFF0288D1) else Color(0xFFEF6C00) // Xanh đậm / Cam đậm

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onCategoryClick(category) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    shape = MaterialTheme.shapes.large, // Bo góc lớn (16.dp)
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = category.categoryName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            // Tag hiển thị Type
                            Surface(
                                color = tagBgColor,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = category.type.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = tagTextColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Nút mũi tên tròn
                        Surface(
                            shape = MaterialTheme.shapes.extraLarge,
                            color = MaterialTheme.colorScheme.background, // Màu xám nhạt nền app
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Go",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            if (filteredCategories.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No categories found", color = Color.Gray)
                    }
                }
            }
        }
    }
}