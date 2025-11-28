package com.example.relisapp.nam.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.* // Import quan trọng cho remember, mutableStateOf, collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.relisapp.nam.viewmodel.CommentModerationViewModel
import com.example.relisapp.nam.viewmodel.ModerationUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminModerationScreen(
    vm: CommentModerationViewModel
) {
    // Collect state từ ViewModel
    val uiState by vm.uiState.collectAsState()

    // State local cho search bar
    var search by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kiểm duyệt bình luận") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Search Bar
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Tìm nội dung...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    if (search.isNotEmpty()) {
                        IconButton(onClick = { search = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            // Xử lý UiState
            when (val state = uiState) {

                is ModerationUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is ModerationUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Lỗi: ${state.message}", color = MaterialTheme.colorScheme.error)
                    }
                }

                is ModerationUiState.Success -> {
                    // Filter list theo từ khóa search
                    val list = state.comments.filter {
                        it.comment.content.contains(search, ignoreCase = true) ||
                                it.userName.contains(search, ignoreCase = true)
                    }

                    if (list.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Không tìm thấy bình luận nào.", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(list, key = { it.comment.commentId }) { item ->
                                ModerationCommentCardEnhanced(
                                    commentWithDetails = item,
                                    onDelete = {
                                        // Gọi hàm xóa trong VM
                                        vm.deleteComment(item.comment)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}