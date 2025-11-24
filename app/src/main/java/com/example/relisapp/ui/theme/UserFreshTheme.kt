package com.example.relisapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography     // ✔ IMPORT ĐÚNG
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// --- COLORS ---
private val PrimaryBlue = Color(0xFF00A9FF)
private val SecondaryMint = Color(0xFF00D2FC)
private val AccentCoral = Color(0xFFFF708D)
private val BackgroundWhite = Color(0xFFF5F8FA)
private val SurfaceWhite = Color(0xFFFFFFFF)
private val TextBlack = Color(0xFF2D3436)

// --- COLOR SCHEME ---
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

    outline = Color.LightGray,
    outlineVariant = Color.LightGray.copy(alpha = 0.5f)
)

// --- THEME ---
@Composable
fun UserFreshTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FreshColorScheme,
       /* typography = Typography,     // ✔ KHÔNG LỖI*/
        content = content
    )
}
