package com.example.relisapp.nam.ui.screens.home
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relisapp.MainActivity
import com.example.relisapp.R
import com.example.relisapp.nam.data.local.SessionManager
import com.example.relisapp.nam.ui.screens.admin.AdminDashboardActivity2
import com.example.relisapp.nam.ui.screens.auth.LoginActivity
import com.example.relisapp.nam.ui.screens.auth.SignupActivity
import com.example.relisapp.nam.ui.theme.BluePrimary
import com.example.relisapp.nam.ui.theme.LearnTheme
import com.example.relisapp.nam.ui.theme.White

class StartActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val session = SessionManager(this)

        // ⭐ Điều hướng nhanh nếu đã đăng nhập
        if (session.isLoggedIn()) {
            when (session.getRole()) {
                "admin" -> {
                    startActivity(Intent(this, AdminDashboardActivity2::class.java))
                    finish()
                    return
                }

                "user" -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    return
                }
            }
        }

        // ⭐ Chưa đăng nhập → Hiển thị màn hình Start
        setContent {
            LearnTheme {
                StartScreen(
                    onLoginClick = {
                        startActivity(Intent(this, LoginActivity::class.java))
                    },
                    onSignUpClick = {
                        startActivity(Intent(this, SignupActivity::class.java))
                    }
                )
            }
        }
    }
}

// ... (Phần Composable StartScreen giữ nguyên như cũ của bạn)
@Composable
fun StartScreen(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAF0FF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 60.dp)
            )

            // LOGIN BUTTON
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary,
                    contentColor = White
                )
            ) {
                Text("ĐĂNG NHẬP", fontSize = 18.sp)
            }

            Spacer(Modifier.height(20.dp))

            // SIGN UP BUTTON
            OutlinedButton(
                onClick = onSignUpClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = White,
                    contentColor = BluePrimary
                ),
                border = BorderStroke(1.5.dp, BluePrimary)
            ) {
                Text("ĐĂNG KÝ", fontSize = 18.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStartScreen() {
    LearnTheme {
        StartScreen(onLoginClick = {}, onSignUpClick = {})
    }
}