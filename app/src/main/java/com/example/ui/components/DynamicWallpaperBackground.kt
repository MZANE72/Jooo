package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.example.model.LiveTheme
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun DynamicWallpaperBackground(
    liveTheme: LiveTheme,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wallpaper_animation")

    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "anim_progress"
    )

    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    // Pre-calculate random stars or particles so they don't jump every frame
    val particleOffsets = remember {
        List(30) {
            Triple(Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 3f + 1f)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        val startBg = if (isDarkMode) Color(liveTheme.bgGradientStart) else Color(0xFFF1F5F9)
        val endBg = if (isDarkMode) Color(liveTheme.bgGradientEnd) else Color(0xFFE2E8F0)
        val primaryAcc = Color(liveTheme.primaryColorHex)
        val secondaryAcc = Color(liveTheme.secondaryColorHex)

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Base gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(startBg, endBg),
                    startY = 0f,
                    endY = height
                )
            )

            when (liveTheme) {
                LiveTheme.NEON_CYBER -> {
                    // Glowing radial lights
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(primaryAcc.copy(alpha = 0.22f * pulseGlow), Color.Transparent),
                            center = Offset(width * 0.8f, height * 0.15f),
                            radius = width * 0.7f
                        ),
                        center = Offset(width * 0.8f, height * 0.15f),
                        radius = width * 0.7f
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(secondaryAcc.copy(alpha = 0.18f * pulseGlow), Color.Transparent),
                            center = Offset(width * 0.15f, height * 0.85f),
                            radius = width * 0.65f
                        ),
                        center = Offset(width * 0.15f, height * 0.85f),
                        radius = width * 0.65f
                    )

                    // Cyber perspective grid lines
                    val gridAlpha = if (isDarkMode) 0.07f else 0.04f
                    for (i in 0..10) {
                        val y = height * (0.6f + (i * 0.04f))
                        drawLine(
                            color = primaryAcc.copy(alpha = gridAlpha),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1.5f
                        )
                    }

                    // Floating neon particles
                    particleOffsets.forEach { (rx, ry, radius) ->
                        val py = ((ry + animProgress) % 1f) * height
                        val px = rx * width
                        drawCircle(
                            color = primaryAcc.copy(alpha = 0.35f),
                            radius = radius,
                            center = Offset(px, py)
                        )
                    }
                }

                LiveTheme.AURORA -> {
                    // Flowing Aurora Borealis curved waves
                    val wavePath1 = Path()
                    val wavePath2 = Path()
                    wavePath1.moveTo(0f, height * 0.2f)
                    wavePath2.moveTo(0f, height * 0.35f)

                    val steps = 8
                    for (i in 0..steps) {
                        val x = width * (i.toFloat() / steps)
                        val angle = animProgress * 2 * Math.PI.toFloat() + (i * 0.8f)
                        val y1 = height * 0.22f + sin(angle) * (height * 0.08f)
                        val y2 = height * 0.38f + cos(angle * 0.8f) * (height * 0.07f)
                        if (i == 0) {
                            wavePath1.moveTo(x, y1)
                            wavePath2.moveTo(x, y2)
                        } else {
                            wavePath1.lineTo(x, y1)
                            wavePath2.lineTo(x, y2)
                        }
                    }
                    wavePath1.lineTo(width, 0f)
                    wavePath1.lineTo(0f, 0f)
                    wavePath1.close()

                    drawPath(
                        path = wavePath1,
                        brush = Brush.verticalGradient(
                            colors = listOf(primaryAcc.copy(alpha = 0.16f * pulseGlow), Color.Transparent),
                            startY = 0f,
                            endY = height * 0.45f
                        )
                    )

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(secondaryAcc.copy(alpha = 0.2f * pulseGlow), Color.Transparent),
                            center = Offset(width * 0.3f, height * 0.25f),
                            radius = width * 0.6f
                        ),
                        center = Offset(width * 0.3f, height * 0.25f),
                        radius = width * 0.6f
                    )
                }

                LiveTheme.COSMIC -> {
                    // Nebular dust clouds
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(primaryAcc.copy(alpha = 0.25f * pulseGlow), Color.Transparent),
                            center = Offset(width * 0.5f, height * 0.3f),
                            radius = width * 0.85f
                        ),
                        center = Offset(width * 0.5f, height * 0.3f),
                        radius = width * 0.85f
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(secondaryAcc.copy(alpha = 0.2f * pulseGlow), Color.Transparent),
                            center = Offset(width * 0.8f, height * 0.7f),
                            radius = width * 0.75f
                        ),
                        center = Offset(width * 0.8f, height * 0.7f),
                        radius = width * 0.75f
                    )

                    // Twinkling stars
                    particleOffsets.forEachIndexed { idx, (rx, ry, radius) ->
                        val twinkle = sin(animProgress * 10f + idx) * 0.4f + 0.6f
                        val starColor = if (idx % 2 == 0) primaryAcc else secondaryAcc
                        drawCircle(
                            color = starColor.copy(alpha = (0.25f + twinkle * 0.55f).coerceIn(0f, 0.9f)),
                            radius = radius * twinkle,
                            center = Offset(rx * width, ry * height)
                        )
                    }
                }

                LiveTheme.RETRO_AMBER -> {
                    // Warm glowing vacuum tube radio back-light
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(primaryAcc.copy(alpha = 0.28f * pulseGlow), Color.Transparent),
                            center = Offset(width * 0.5f, height * 0.18f),
                            radius = width * 0.75f
                        ),
                        center = Offset(width * 0.5f, height * 0.18f),
                        radius = width * 0.75f
                    )

                    // Subtle horizontal scanning warm lines
                    for (i in 0..15) {
                        val y = height * (i * 0.065f)
                        drawLine(
                            color = primaryAcc.copy(alpha = 0.04f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 2f
                        )
                    }
                }

                LiveTheme.MIDNIGHT_OCEAN -> {
                    // Deep ocean undulating light waves
                    val oceanPath = Path()
                    val steps = 10
                    oceanPath.moveTo(0f, height * 0.5f)
                    for (i in 0..steps) {
                        val x = width * (i.toFloat() / steps)
                        val angle = animProgress * 3 * Math.PI.toFloat() + (i * 0.6f)
                        val y = height * 0.65f + sin(angle) * (height * 0.05f)
                        oceanPath.lineTo(x, y)
                    }
                    oceanPath.lineTo(width, height)
                    oceanPath.lineTo(0f, height)
                    oceanPath.close()

                    drawPath(
                        path = oceanPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(primaryAcc.copy(alpha = 0.15f * pulseGlow), secondaryAcc.copy(alpha = 0.25f)),
                            startY = height * 0.6f,
                            endY = height
                        )
                    )

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(secondaryAcc.copy(alpha = 0.2f * pulseGlow), Color.Transparent),
                            center = Offset(width * 0.8f, height * 0.2f),
                            radius = width * 0.6f
                        ),
                        center = Offset(width * 0.8f, height * 0.2f),
                        radius = width * 0.6f
                    )
                }
            }
        }

        // Foreground content container
        content()
    }
}
