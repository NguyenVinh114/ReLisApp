package com.example.relisapp.nam.ui.components.dialogs

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relisapp.nam.model.StreakMilestone  // ⭐ SỬA IMPORT ĐÚNG

@Composable
fun MilestoneAchievedDialog(
    milestone: StreakMilestone,
    onDismiss: () -> Unit
) {
    // Animation scale emoji
    val infiniteTransition = rememberInfiniteTransition(label = "emoji_anim")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text(
                text = milestone.emoji,
                fontSize = 60.sp,
                modifier = Modifier.scale(scale),
                textAlign = TextAlign.Center
            )
        },
        title = {
            Text(
                text = "🎉 Chúc mừng! 🎉",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6B35)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Bạn đã đạt được:",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = milestone.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B35)
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${milestone.days} ngày liên tiếp!",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = milestone.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B35)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tiếp tục phát huy!",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        containerColor = Color.White,
        shape = MaterialTheme.shapes.large
    )
}
