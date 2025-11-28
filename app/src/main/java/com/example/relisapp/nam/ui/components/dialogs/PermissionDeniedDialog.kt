package com.example.relisapp.nam.ui.components.dialogs

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun PermissionDeniedDialog(
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cần quyền thông báo") },
        text = { Text("Hãy bật quyền thông báo để nhận nhắc nhở học tập mỗi ngày.") },
        confirmButton = {
            Button(onClick = onOpenSettings) { Text("Mở cài đặt") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Để sau") }
        }
    )
}
