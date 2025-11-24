package com.example.relisapp.phat.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// --- 1. BẢNG MÀU RIÊNG (LOCAL COLORS) ---
private val PrimaryBlue = Color(0xFF00A9FF)      // Xanh dương tươi
private val SecondaryMint = Color(0xFF00D2FC)    // Xanh mint
private val AccentCoral = Color(0xFFFF708D)      // Hồng san hô
private val BackgroundWhite = Color(0xFFF5F8FA)  // Trắng xám nhẹ
private val SurfaceWhite = Color(0xFFFFFFFF)     // Trắng tinh
private val TextBlack = Color(0xFF2D3436)        // Đen xám

// --- 2. TẠO COLOR SCHEME ---
private val FreshColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = PrimaryBlue.copy(alpha = 0.1f),
    onPrimaryContainer = PrimaryBlue,

    secondary = AccentCoral,
    onSecondary = Color.White,
    secondaryContainer = AccentCoral.copy(alpha = 0.1f),
    onSecondaryContainer = AccentCoral,

    background = BackgroundWhite,
    surface = SurfaceWhite,
    onSurface = TextBlack,
    onBackground = TextBlack,

    // Thiết lập màu viền (outline) cho TextField để nó không bị xám xịt
    outline = Color.LightGray,
    outlineVariant = Color.LightGray.copy(alpha = 0.5f)
)

// --- 3. COMPOSABLE THEME RIÊNG CHO USER ---
@Composable
fun UserFreshTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Ở đây mình ép cứng lightColorScheme (FreshColorScheme)
    // Nếu bạn muốn support DarkMode sau này thì thêm logic vào đây

    MaterialTheme(
        colorScheme = FreshColorScheme,
        typography = Typography, // Có thể import Typography gốc nếu muốn giữ font chữ
        content = content
    )
}