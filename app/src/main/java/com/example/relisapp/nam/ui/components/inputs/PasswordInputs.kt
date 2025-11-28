package com.example.relisapp.nam.ui.components.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.relisapp.R

@Composable
fun PasswordInput(
    label: String = "Mật khẩu",
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true
) {
    var isVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        enabled = enabled,   // ✔ THÊM DÒNG NÀY
        visualTransformation = if (isVisible)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(
                onClick = { isVisible = !isVisible },
                enabled = enabled   // ✔ Disable icon khi loading
            ) {
                Icon(
                    painter = painterResource(
                        if (isVisible) R.drawable.baseline_visibility_off_24
                        else R.drawable.baseline_visibility_24
                    ),
                    contentDescription = null
                )
            }
        }
    )
}


@Composable
fun ConfirmPasswordInput(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true
) {
    var isVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Xác nhận mật khẩu") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        enabled = enabled,
        visualTransformation = if (isVisible)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(
                onClick = { isVisible = !isVisible },
                enabled = enabled
            ) {
                Icon(
                    painter = painterResource(
                        if (isVisible) R.drawable.baseline_visibility_off_24
                        else R.drawable.baseline_visibility_24
                    ),
                    contentDescription = null
                )
            }
        }
    )
}

