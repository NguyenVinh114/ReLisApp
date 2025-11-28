package com.example.relisapp.nam.ui.screens.streak

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.relisapp.R

@Composable
fun AnimatedFireIcon(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fire")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isActive) 1.15f else 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isActive) 1f else 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Icon(
        painter = painterResource(R.drawable.ic_fire),
        contentDescription = null,
        tint = if (isActive) Color(0xFFFF6B35) else Color.Gray,
        modifier = modifier
            .scale(scale)
            .alpha(alpha)
    )
}

@Composable
fun AnimatedStreakNumber(
    targetValue: Int,
    modifier: Modifier = Modifier
) {
    var currentValue by remember { mutableStateOf(0) }

    LaunchedEffect(targetValue) {
        animate(
            initialValue = currentValue.toFloat(),
            targetValue = targetValue.toFloat(),
            animationSpec = tween(
                durationMillis = 900,
                easing = FastOutSlowInEasing
            )
        ) { value, _ ->
            currentValue = value.toInt()
        }
    }

    Text(
        text = "$currentValue",
        style = MaterialTheme.typography.displayLarge,
        color = Color(0xFFFF6B35),
        modifier = modifier
    )
}

@Composable
fun AnimatedProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "progress"
    )

    LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = modifier,
        color = Color(0xFFFF6B35),
        trackColor = Color(0xFFFFE0B2)
    )
}

@Composable
fun AnimatedCard(
    visible: Boolean,
    delay: Int = 0,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(500, delayMillis = delay))
                + slideInVertically(
            animationSpec = tween(500, delayMillis = delay),
            initialOffsetY = { it / 4 }
        ),
        exit = fadeOut() + slideOutVertically()
    ) {
        content()
    }
}
