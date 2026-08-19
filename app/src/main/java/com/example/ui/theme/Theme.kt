package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MonochromeLightColorScheme = lightColorScheme(
    primary = MonochromeLightPrimary,
    onPrimary = MonochromeLightOnPrimary,
    primaryContainer = MonochromeLightSurfaceVariant,
    onPrimaryContainer = MonochromeLightPrimary,
    secondary = MonochromeLightPrimary,
    onSecondary = MonochromeLightOnPrimary,
    tertiary = MonochromeLightTextSecondary,
    onTertiary = MonochromeLightOnPrimary,
    background = MonochromeLightBg,
    onBackground = MonochromeLightTextPrimary,
    surface = MonochromeLightSurface,
    onSurface = MonochromeLightTextPrimary,
    surfaceVariant = MonochromeLightSurfaceVariant,
    onSurfaceVariant = MonochromeLightTextSecondary,
    outline = MonochromeLightBorder,
    outlineVariant = MonochromeLightBorderLight
)

private val MonochromeDarkColorScheme = darkColorScheme(
    primary = MonochromeDarkPrimary,
    onPrimary = MonochromeDarkOnPrimary,
    primaryContainer = MonochromeDarkSurfaceVariant,
    onPrimaryContainer = MonochromeDarkPrimary,
    secondary = MonochromeDarkPrimary,
    onSecondary = MonochromeDarkOnPrimary,
    tertiary = MonochromeDarkTextSecondary,
    onTertiary = MonochromeDarkOnPrimary,
    background = MonochromeDarkBg,
    onBackground = MonochromeDarkTextPrimary,
    surface = MonochromeDarkSurface,
    onSurface = MonochromeDarkTextPrimary,
    surfaceVariant = MonochromeDarkSurfaceVariant,
    onSurfaceVariant = MonochromeDarkTextSecondary,
    outline = MonochromeDarkBorder,
    outlineVariant = MonochromeDarkBorderLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) MonochromeDarkColorScheme else MonochromeLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
