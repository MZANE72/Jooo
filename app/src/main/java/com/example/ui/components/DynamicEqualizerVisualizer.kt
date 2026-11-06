package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.PlaybackStatus
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta

@Composable
fun EqualizerFrequencyBars(
    frequencies: List<Float>,
    isPlaying: Boolean,
    primaryColor: Color = NeonCyan,
    secondaryColor: Color = NeonMagenta,
    modifier: Modifier = Modifier,
    barCount: Int = 16,
    height: Dp = 60.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val totalWidth = size.width
        val canvasHeight = size.height
        val barWidth = (totalWidth / (barCount * 1.5f)).coerceAtLeast(4f)
        val space = (totalWidth - (barWidth * barCount)) / (barCount + 1)

        for (i in 0 until barCount) {
            val freq = if (isPlaying && i < frequencies.size) frequencies[i] else 0.08f
            val barHeight = (canvasHeight * freq).coerceIn(6f, canvasHeight)
            val x = space + i * (barWidth + space)
            val y = canvasHeight - barHeight

            // Gradient bar
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(primaryColor, secondaryColor),
                    startY = y,
                    endY = canvasHeight
                ),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )

            // Glowing top peak dot
            if (isPlaying && freq > 0.2f) {
                drawCircle(
                    color = primaryColor.copy(alpha = 0.9f),
                    radius = barWidth * 0.7f,
                    center = Offset(x + barWidth / 2f, y - 3f)
                )
            }
        }
    }
}

@Composable
fun CircularEqualizerPulse(
    isPlaying: Boolean,
    primaryColor: Color = NeonCyan,
    secondaryColor: Color = NeonMagenta,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 200.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_rings")

    val ring1 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_1"
    )

    val ring2 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, delayMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_2"
    )

    if (isPlaying) {
        Canvas(modifier = modifier.size(sizeDp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = size.width / 2.6f

            // Ring 1
            val alpha1 = ((1.35f - ring1) / 0.55f).coerceIn(0f, 0.45f)
            drawCircle(
                color = primaryColor.copy(alpha = alpha1),
                radius = baseRadius * ring1,
                center = center
            )

            // Ring 2
            val alpha2 = ((1.35f - ring2) / 0.55f).coerceIn(0f, 0.35f)
            drawCircle(
                color = secondaryColor.copy(alpha = alpha2),
                radius = baseRadius * ring2,
                center = center
            )
        }
    }
}

@Composable
fun MiniEqualizerBadge(
    isPlaying: Boolean,
    color: Color = NeonCyan,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "mini_eq")

    val h1 by transition.animateFloat(
        initialValue = 0.3f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(400), RepeatMode.Reverse),
        label = "h1"
    )
    val h2 by transition.animateFloat(
        initialValue = 0.8f, targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(550), RepeatMode.Reverse),
        label = "h2"
    )
    val h3 by transition.animateFloat(
        initialValue = 0.2f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(350), RepeatMode.Reverse),
        label = "h3"
    )
    val h4 by transition.animateFloat(
        initialValue = 0.6f, targetValue = 0.15f,
        animationSpec = infiniteRepeatable(tween(480), RepeatMode.Reverse),
        label = "h4"
    )

    Row(
        modifier = modifier
            .height(16.dp)
            .padding(horizontal = 2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        val bars = listOf(h1, h2, h3, h4)
        bars.forEach { factor ->
            val barHeight = if (isPlaying) (16.dp * factor).coerceAtLeast(3.dp) else 3.dp
            Surface(
                modifier = Modifier
                    .padding(horizontal = 1.dp)
                    .width(3.dp)
                    .height(barHeight),
                shape = RoundedCornerShape(1.5.dp),
                color = color
            ) {}
        }
    }
}
