package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.model.LiveTheme

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF00363D),
    onPrimaryContainer = NeonCyan,
    secondary = NeonMagenta,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4A0023),
    onSecondaryContainer = Color(0xFFFFB2D1),
    tertiary = NeonAmber,
    background = DarkBgMain,
    onBackground = TextWhitePrimary,
    surface = DarkSurfaceElevated,
    onSurface = TextWhitePrimary,
    surfaceVariant = DarkSurfaceCard,
    onSurfaceVariant = TextMuted,
    outline = DarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF007A8A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB8F0F7),
    onPrimaryContainer = Color(0xFF00363D),
    secondary = Color(0xFFB00055),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFD9E4),
    onSecondaryContainer = Color(0xFF3E001B),
    tertiary = Color(0xFF8A5B00),
    background = LightBgMain,
    onBackground = Color(0xFF0F172A),
    surface = LightSurfaceElevated,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = LightSurfaceCard,
    onSurfaceVariant = Color(0xFF475569),
    outline = LightOutline
)

@Composable
fun RadyoTheme(
    darkTheme: Boolean = true,
    activeLiveTheme: LiveTheme = LiveTheme.NEON_CYBER,
    content: @Composable () -> Unit
) {
    val dynamicColors = if (darkTheme) {
        DarkColorScheme.copy(
            primary = Color(activeLiveTheme.primaryColorHex),
            secondary = Color(activeLiveTheme.secondaryColorHex)
        )
    } else {
        LightColorScheme.copy(
            primary = Color(activeLiveTheme.primaryColorHex),
            secondary = Color(activeLiveTheme.secondaryColorHex)
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = Color.Transparent.toArgb()
                window.navigationBarColor = Color.Transparent.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = dynamicColors,
        typography = Typography,
        content = content
    )
}
