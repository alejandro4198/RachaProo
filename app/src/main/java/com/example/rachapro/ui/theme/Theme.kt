package com.example.rachapro.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat

private val RachaProColorScheme = lightColorScheme(
    primary = RachaIndigo,
    onPrimary = RachaSurface,
    primaryContainer = RachaSurfaceVariant,
    onPrimaryContainer = RachaIndigoDark,
    inversePrimary = RachaViolet,

    secondary = RachaCyan,
    onSecondary = RachaSurface,
    secondaryContainer = RachaCyan.copy(alpha = 0.15f),
    onSecondaryContainer = RachaOnBackground,

    tertiary = RachaCoral,
    onTertiary = RachaSurface,
    tertiaryContainer = RachaCoral.copy(alpha = 0.15f),
    onTertiaryContainer = RachaOnBackground,

    background = RachaBackground,
    onBackground = RachaOnBackground,

    surface = RachaSurface,
    onSurface = RachaOnSurface,
    surfaceVariant = RachaSurfaceVariant,
    onSurfaceVariant = RachaOnSurfaceMuted,
    surfaceTint = RachaIndigo,
    inverseSurface = RachaOnBackground,
    inverseOnSurface = RachaSurface,

    surfaceContainerLowest = RachaSurface,
    surfaceContainerLow = Color(0xFFFBFBFE),
    surfaceContainer = RachaSurfaceMuted,
    surfaceContainerHigh = RachaSurfaceVariant,
    surfaceContainerHighest = Color(0xFFE7E8F6),

    outline = RachaOutline,
    outlineVariant = Color(0xFFEEEEF7),

    error = RachaError,
    onError = RachaSurface,
    errorContainer = Color(0xFFFDE7E8),
    onErrorContainer = Color(0xFF8E1B22)
)

@Composable
fun RachaProTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = RachaProColorScheme

    val view = androidx.compose.ui.platform.LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = RachaShapes,
        content = content
    )
}
