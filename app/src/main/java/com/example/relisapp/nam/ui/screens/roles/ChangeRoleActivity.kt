package com.example.relisapp.nam.ui.screens.roles

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.relisapp.nam.data.repository.UserRepository
import com.example.relisapp.nam.database.AppDatabase
import com.example.relisapp.nam.ui.theme.BluePrimary
import com.example.relisapp.nam.ui.theme.GrayText
import com.example.relisapp.nam.ui.theme.LearnTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChangeRoleActivity : ComponentActivity() {

    private lateinit var repository: UserRepository
    private var currentRole by mutableStateOf("")
    private var selectedRole by mutableStateOf("")
    private var userId = -1
    private var isLoading by mutableStateOf(false)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.Companion.getDatabase(this)
        repository = UserRepository(database.userDao())

        userId = intent.getIntExtra("USER_ID", -1)
        currentRole = intent.getStringExtra("CURRENT_ROLE") ?: "user"
        selectedRole = currentRole

        if (userId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy user", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setContent {
            LearnTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Thay đổi vai trò") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(Icons.Default.Close, "Đóng")
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                ) { padding ->
                    ChangeRoleContent(
                        modifier = Modifier.Companion.padding(padding),
                        selectedRole = selectedRole,
                        onRoleSelected = { selectedRole = it },
                        onSave = { saveRole() },
                        onCancel = { finish() },
                        isLoading = isLoading
                    )
                }
            }
        }
    }

    @Composable
    private fun ChangeRoleContent(
        modifier: Modifier = Modifier.Companion,
        selectedRole: String,
        onRoleSelected: (String) -> Unit,
        onSave: () -> Unit,
        onCancel: () -> Unit,
        isLoading: Boolean
    ) {
        val roles = listOf(
            "user" to "Người dùng thường",
            "mod" to "Moderator",
            "admin" to "Quản trị viên"
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Chọn vai trò mới:",
                fontSize = 18.sp,
                fontWeight = FontWeight.Companion.Bold,
                modifier = Modifier.Companion.padding(bottom = 8.dp)
            )

            Text(
                "Vai trò hiện tại: ${currentRole.uppercase()}",
                fontSize = 14.sp,
                color = GrayText
            )

            Spacer(Modifier.Companion.height(8.dp))

            // Role options
            Card(
                modifier = Modifier.Companion.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column {
                    roles.forEachIndexed { index, (role, label) ->
                        Row(
                            Modifier.Companion
                                .fillMaxWidth()
                                .selectable(
                                    selected = (role == selectedRole),
                                    onClick = { onRoleSelected(role) },
                                    role = Role.Companion.RadioButton,
                                    enabled = !isLoading
                                )
                                .padding(16.dp),
                            verticalAlignment = Alignment.Companion.CenterVertically
                        ) {
                            RadioButton(
                                selected = (role == selectedRole),
                                onClick = null,
                                enabled = !isLoading,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = BluePrimary
                                )
                            )
                            Spacer(Modifier.Companion.width(16.dp))
                            Column {
                                Text(
                                    text = label,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Companion.Medium
                                )
                                Text(
                                    text = role,
                                    fontSize = 12.sp,
                                    color = GrayText
                                )
                            }
                        }
                        if (index < roles.size - 1) {
                            HorizontalDivider()
                        }
                    }
                }
            }

            if (selectedRole != currentRole) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.Companion.padding(12.dp),
                        verticalAlignment = Alignment.Companion.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(Modifier.Companion.width(8.dp))
                        Text(
                            "Thay đổi vai trò từ ${currentRole.uppercase()} → ${selectedRole.uppercase()}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            Spacer(Modifier.Companion.weight(1f))

            // Action buttons
            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.Companion.weight(1f),
                    enabled = !isLoading
                ) {
                    Text("Hủy")
                }

                Button(
                    onClick = onSave,
                    modifier = Modifier.Companion.weight(1f),
                    enabled = selectedRole != currentRole && !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.Companion.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Lưu")
                    }
                }
            }
        }
    }

    private fun saveRole() {
        isLoading = true
        lifecycleScope.launch {
            try {
                // Cập nhật role trong database bằng cách update User object
                withContext(Dispatchers.IO) {
                    val user = repository.getUserById(userId)
                    user?.let {
                        val updatedUser = it.copy(role = selectedRole)
                        repository.updateUser(updatedUser)
                    }
                }

                Toast.makeText(
                    this@ChangeRoleActivity,
                    "Đã cập nhật vai trò thành công",
                    Toast.LENGTH_SHORT
                ).show()

                setResult(RESULT_OK)
                finish()
            } catch (e: Exception) {
                Toast.makeText(
                    this@ChangeRoleActivity,
                    "Lỗi: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                isLoading = false
            }
        }
    }
}