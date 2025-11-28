package com.example.relisapp.nam.ui.components.calendar


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthYearPickerBottomSheet(
    currentMonth: Int,
    currentYear: Int,
    onDismiss: () -> Unit,
    onApply: (Int, Int) -> Unit
) {
    var selectedMonth by remember { mutableStateOf(currentMonth) }
    var selectedYear by remember { mutableStateOf(currentYear) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Chọn tháng & năm",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                // ---- MONTH LIST ----
                LazyColumn(
                    modifier = Modifier
                        .height(250.dp)
                        .width(120.dp)
                ) {
                    items((1..12).toList()) { month ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp)
                                .background(
                                    if (month == selectedMonth) Color(0xFFFFE0B2)
                                    else Color(0xFFF5F5F5),
                                    shape = MaterialTheme.shapes.small
                                )
                                .clickable { selectedMonth = month }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tháng $month",
                                color = if (month == selectedMonth) Color(0xFFD84315) else Color.Gray
                            )
                        }
                    }
                }

                // ---- YEAR LIST ----
                LazyColumn(
                    modifier = Modifier
                        .height(250.dp)
                        .width(120.dp)
                ) {
                    items((2000..2100).toList()) { year ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp)
                                .background(
                                    if (year == selectedYear) Color(0xFFBBDEFB)
                                    else Color(0xFFF5F5F5),
                                    shape = MaterialTheme.shapes.small
                                )
                                .clickable { selectedYear = year }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$year",
                                color = if (year == selectedYear) Color(0xFF1565C0) else Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    onApply(selectedMonth, selectedYear)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B35)
                )
            ) {
                Text("Áp dụng")
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}
