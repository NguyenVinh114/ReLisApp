package com.example.relisapp.nam.ui.screens.settings

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.relisapp.nam.viewmodel.ReminderSettingsViewModel
import com.example.relisapp.nam.ui.components.dialogs.PermissionDeniedDialog


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ReminderSettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showTimePicker by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onPermissionResult(granted)
        if (!granted) showPermissionDialog = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cài đặt") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {

            item {
                SettingItem(
                    title = "Thông báo hằng ngày",
                    subtitle =
                        if (uiState.isReminderEnabled)
                            "Bật — ${viewModel.getReminderTimeString()}"
                        else "Tắt",
                    icon = Icons.Default.Notifications,

                    content = {
                        Switch(
                            checked = uiState.isReminderEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled && !uiState.isPermissionGranted) {
                                    if (Build.VERSION.SDK_INT >= 33)
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    else
                                        viewModel.toggleReminder(true)
                                } else {
                                    viewModel.toggleReminder(enabled)
                                }
                            }
                        )
                    }
                )
            }

            // Only show if reminder is enabled
            if (uiState.isReminderEnabled) {
                item {
                    SettingItem(
                        title = "Chọn giờ nhắc nhở",
                        subtitle = viewModel.getReminderTimeString(),
                        icon = Icons.Default.Schedule,
                        onClick = { showTimePicker = true }
                    )
                }
            }
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            currentHour = uiState.reminderHour,
            currentMinute = uiState.reminderMinute,
            onTimeSelected = { h, m ->
                viewModel.setReminderTime(h, m)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }

    if (showPermissionDialog) {
        PermissionDeniedDialog(
            onDismiss = { showPermissionDialog = false },
            onOpenSettings = {
                context.startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                )
            }
        )
    }
}


@Composable
fun TimePickerDialog(
    currentHour: Int,
    currentMinute: Int,
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedHour by remember { mutableIntStateOf(currentHour) }
    var selectedMinute by remember { mutableIntStateOf(currentMinute) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = Color(0xFFFF6B35),
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = "Chọn giờ nhắc nhở",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimeUnitPicker(
                        value = selectedHour,
                        range = 0..23,
                        onValueChange = { selectedHour = it },
                        label = "Giờ"
                    )

                    Text(
                        ":",
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    TimeUnitPicker(
                        value = selectedMinute,
                        range = 0..59,
                        onValueChange = { selectedMinute = it },
                        label = "Phút"
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onTimeSelected(selectedHour, selectedMinute) }) {
                Text("Xác nhận")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

@Composable
fun SettingItem(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    onClick: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(24.dp))

            Spacer(Modifier.width(16.dp))

            // Nếu content != null → HIỂN THỊ TITLE BÊN TRÁI + CONTENT BÊN PHẢI
            if (content != null) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.bodyLarge)
                    if (subtitle != null)
                        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }

                content()
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.bodyLarge)
                    if (subtitle != null)
                        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }

                Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
            }
        }
    }
}


@SuppressLint("DefaultLocale")
@Composable
fun TimeUnitPicker(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = {
                val newValue = if (value < range.last) value + 1 else range.first
                onValueChange(newValue)
            }
        ) {
            Icon(Icons.Default.KeyboardArrowUp, null)
        }

        Text(
            text = String.format("%02d", value),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold
        )

        IconButton(
            onClick = {
                val newValue = if (value > range.first) value - 1 else range.last
                onValueChange(newValue)
            }
        ) {
            Icon(Icons.Default.KeyboardArrowDown, null)
        }

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}
