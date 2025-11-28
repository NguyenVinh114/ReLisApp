package com.example.relisapp.nam.ui.components.dialogs

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight

@Composable
fun TimePickerDialog(
    currentHour: Int,
    currentMinute: Int,
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var hour by remember { mutableStateOf(currentHour) }
    var minute by remember { mutableStateOf(currentMinute) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chọn thời gian", fontWeight = FontWeight.Bold) },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                NumberPicker(
                    value = hour,
                    range = 0..23,
                    label = "Giờ",
                    onValueChange = { hour = it }
                )

                Spacer(modifier = Modifier.width(16.dp))

                NumberPicker(
                    value = minute,
                    range = 0..59,
                    label = "Phút",
                    onValueChange = { minute = it }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onTimeSelected(hour, minute) }) {
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
fun NumberPicker(
    value: Int,
    range: IntRange,
    label: String,
    onValueChange: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = {
            val next = if (value == range.last) range.first else value + 1
            onValueChange(next)
        }) {
            Icon(Icons.Default.KeyboardArrowUp, null)
        }

        Text(
            text = String.format("%02d", value),
            style = MaterialTheme.typography.headlineMedium
        )

        IconButton(onClick = {
            val next = if (value == range.first) range.last else value - 1
            onValueChange(next)
        }) {
            Icon(Icons.Default.KeyboardArrowDown, null)
        }

        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}
