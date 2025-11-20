package com.example.relisapp.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relisapp.R
import com.example.relisapp.ui.components.PasswordInput
import com.example.relisapp.ui.components.ConfirmPasswordInput
import com.example.relisapp.ui.theme.BluePrimary
import com.example.relisapp.ui.theme.ReLisAppTheme
import com.example.relisapp.ui.theme.White

class ChangeForgotPassWordActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ReLisAppTheme {
                ChangePasswordScreen(
                    onBackClick = { finish() },
                    onSaveClick = { pass, confirm ->

                        if (pass.length < 6) {
                            Toast.makeText(
                                this,
                                "Mật khẩu phải có ít nhất 6 ký tự",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@ChangePasswordScreen
                        }

                        if (pass != confirm) {
                            Toast.makeText(
                                this,
                                "Mật khẩu xác nhận không khớp",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@ChangePasswordScreen
                        }

                        Toast.makeText(
                            this,
                            "Thay đổi mật khẩu thành công",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onBackClick: () -> Unit,
    onSaveClick: (String, String) -> Unit
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thay đổi thông tin") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(110.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Thay đổi thông tin",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Password Input (DÙNG COMPONENT CÓ SẴN CỦA BẠN)
            PasswordInput(
                label = "Mật khẩu mới",
                value = password,
                onValueChange = { password = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Input
            ConfirmPasswordInput(
                value = confirmPassword,
                onValueChange = { confirmPassword = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Button Save
            Button(
                onClick = { onSaveClick(password, confirmPassword) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary,
                    contentColor = White
                )
            ) {
                Text(
                    text = "Lưu thay đổi",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewChangePasswordScreen() {
    ReLisAppTheme {
        ChangePasswordScreen(
            onBackClick = {},
            onSaveClick = { _, _ -> }
        )
    }
}
