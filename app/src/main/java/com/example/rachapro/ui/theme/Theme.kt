package com.example.rachapro.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat

private val RachaProColorScheme = lightColorScheme(
    primary = RachaIndigo,
    onPrimary = RachaSurface,
    primaryContainer = RachaSurfaceVariant,
    onPrimaryContainer = RachaIndigoDark,
    secondary = RachaCyan,
    onSecondary = RachaOnBackground,
    secondaryContainer = RachaCyan.copy(alpha = 0.15f),
    onSecondaryContainer = RachaIndigoDark,
    tertiary = RachaCoral,
    onTertiary = RachaSurface,
    background = RachaBackground,
    onBackground = RachaOnBackground,
    surface = RachaSurface,
    onSurface = RachaOnSurface,
    surfaceVariant = RachaSurfaceVariant,
    onSurfaceVariant = RachaOnSurfaceMuted,
    error = RachaError,
    onError = RachaSurface
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
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = RachaShapes,
        content = content
    )
}
