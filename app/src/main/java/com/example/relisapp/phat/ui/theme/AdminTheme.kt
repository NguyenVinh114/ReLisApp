package com.example.relisapp.phat.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.relisapp.phat.ui.theme.AdminShapes
// --- 1. BẢNG MÀU CHO ADMIN THEME (TÔNG MÀU CHUYÊN NGHIỆP) ---

// Màu chính: Xanh Navy đậm, tạo cảm giác tin cậy, ổn định.
private val AdminPrimary = Color(0xFF0D47A1) // Deep Navy Blue
// Màu nhấn (Secondary/Accent): Xanh dương nhạt hơn để tạo điểm nhấn cho các nút hành động.
private val AdminSecondary = Color(0xFF42A5F5) // Bright Blue
// Màu nền: Xám rất nhạt (gần như trắng) để không gây mỏi mắt.
private val AdminBackground = Color(0xFFF7F9FC)
// Màu cho các bề mặt (Card, Dialog, Drawer): Trắng tinh để nổi bật trên nền.
private val AdminSurface = Color(0xFFFFFFFF)
// Màu chữ chính: Xám đen đậm, có độ tương phản cao.
private val AdminText = Color(0xFF263238) // Dark Slate Grey
// Màu cho các thành phần "Container" (nền của item được chọn, header...)
private val AdminContainer = Color(0xFFE3F2FD) // Very Light Blue
// Màu cho viền (Outline)
private val AdminOutline = Color(0xFFB0BEC5) // Blue Grey
// Màu báo lỗi
private val AdminError = Color(0xFFD32F2F) // Standard Error Red


// --- 2. TẠO COLOR SCHEME (BẢNG MÀU SÁNG - LIGHT) ---
private val AdminLightColorScheme = lightColorScheme(
    primary = AdminPrimary,
    onPrimary = Color.White, // Chữ trên nền màu chính
    primaryContainer = AdminContainer, // Nền cho mục được chọn, header
    onPrimaryContainer = AdminPrimary, // Chữ trên nền container

    secondary = AdminSecondary,
    onSecondary = Color.White,
    secondaryContainer = AdminSecondary.copy(alpha = 0.1f),
    onSecondaryContainer = AdminSecondary,

    background = AdminBackground,
    onBackground = AdminText,

    surface = AdminSurface,
    onSurface = AdminText,
    surfaceVariant = AdminBackground, // Nền phụ, ví dụ nền của content trong BaseAdminScreen
    onSurfaceVariant = AdminText.copy(alpha = 0.7f),

    error = AdminError,
    onError = Color.White,
    errorContainer = AdminError.copy(alpha = 0.1f),
    onErrorContainer = AdminError,

    outline = AdminOutline
)

/*
    Bạn cũng có thể định nghĩa một darkColorScheme ở đây nếu muốn hỗ trợ Dark Mode sau này
    private val AdminDarkColorScheme = darkColorScheme(...)
*/


// --- 3. COMPOSABLE THEME CHO ADMIN ---
@Composable
fun AdminProTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Hiện tại, chúng ta sẽ chỉ sử dụng bảng màu sáng (light scheme)
    val colors = AdminLightColorScheme

    // Nếu muốn hỗ trợ dark theme trong tương lai, bạn chỉ cần bỏ comment đoạn code này
    // val colors = if (useDarkTheme) AdminDarkColorScheme else AdminLightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography, // Sử dụng typography chung
        shapes = AdminShapes,       // Sử dụng shapes chung
        content = content
    )
}
