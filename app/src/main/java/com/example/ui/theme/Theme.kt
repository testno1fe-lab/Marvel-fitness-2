package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MarvelDarkColorScheme = darkColorScheme(
    primary = MarvelRed,
    onPrimary = TextWhite,
    primaryContainer = MarvelRedDark,
    onPrimaryContainer = TextWhite,
    secondary = MarvelRedLight,
    onSecondary = TextWhite,
    secondaryContainer = MarvelSurfaceElevated,
    onSecondaryContainer = TextWhite,
    tertiary = MarvelGold,
    onTertiary = MarvelBlack,
    background = MarvelBlack,
    onBackground = TextWhite,
    surface = MarvelDark,
    onSurface = TextWhite,
    surfaceVariant = MarvelSurface,
    onSurfaceVariant = TextSecondary,
    outline = MarvelBorder,
    outlineVariant = MarvelBorderLight,
    error = MarvelError,
    onError = TextWhite
)

@Composable
fun MarvelFitnessTheme(
    darkTheme: Boolean = true, // Marvel Fitness signature is a sleek dark aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = MarvelDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = MarvelBlack.toArgb()
                window.navigationBarColor = MarvelDark.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Alias for backwards compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MarvelFitnessTheme(content = content)
}
