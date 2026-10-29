package com.muzafrik.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.muzafrik.app.data.settings.ThemeMode

private val LightColors = lightColorScheme(
    primary = DeepGreen, onPrimary = Color.White,
    primaryContainer = Color(0xFFCFE6D8), onPrimaryContainer = Color(0xFF0A2A1D),
    secondary = TerraCotta, onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDBCF), onSecondaryContainer = Color(0xFF3A0F00),
    tertiary = Ocre, onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE3A8), onTertiaryContainer = Color(0xFF3A2700),
    background = Sand, onBackground = Color(0xFF1E1A14),
    surface = Color(0xFFFFFBF3), onSurface = Color(0xFF1E1A14),
    surfaceVariant = Color(0xFFF0E4D0), onSurfaceVariant = Color(0xFF50453A),
    outline = Color(0xFF85766A),
)

private val DarkColors = darkColorScheme(
    primary = Gold, onPrimary = Color(0xFF2B1D00),
    primaryContainer = DeepGreen, onPrimaryContainer = Color(0xFFCFE6D8),
    secondary = Color(0xFFE58A66), onSecondary = Color(0xFF4A1500),
    secondaryContainer = Color(0xFF6E2E15), onSecondaryContainer = Color(0xFFFFDBCF),
    tertiary = Color(0xFFE8A93A), onTertiary = Color(0xFF3A2700),
    tertiaryContainer = Color(0xFF5A3F00), onTertiaryContainer = Color(0xFFFFE3A8),
    background = Earth, onBackground = Color(0xFFEDE0D0),
    surface = Color(0xFF1E1812), onSurface = Color(0xFFEDE0D0),
    surfaceVariant = Color(0xFF3A2F25), onSurfaceVariant = Color(0xFFD3C3B0),
    outline = Color(0xFF9C8C7B),
)

@Composable
fun MuZafrikTheme(themeMode: ThemeMode, content: @Composable () -> Unit) {
    val dark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !dark
                isAppearanceLightNavigationBars = !dark
            }
        }
    }
    MaterialTheme(colorScheme = if (dark) DarkColors else LightColors, typography = Typography, content = content)
}
