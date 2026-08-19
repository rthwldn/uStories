package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Monochrome Core Tokens
val MonochromeLightBg = Color(0xFFFFFFFF)
val MonochromeLightSurface = Color(0xFFF8F8F8)
val MonochromeLightSurfaceVariant = Color(0xFFEEEEEE)
val MonochromeLightSurfaceElevated = Color(0xFFE5E5E5)
val MonochromeLightPrimary = Color(0xFF000000)
val MonochromeLightOnPrimary = Color(0xFFFFFFFF)
val MonochromeLightTextPrimary = Color(0xFF111111)
val MonochromeLightTextSecondary = Color(0xFF555555)
val MonochromeLightBorder = Color(0xFFDCDCDC)
val MonochromeLightBorderLight = Color(0xFFECECEC)

// Dark / Pure Black AMOLED Core Tokens
val MonochromeDarkBg = Color(0xFF000000)
val MonochromeDarkSurface = Color(0xFF0C0C0C)
val MonochromeDarkSurfaceVariant = Color(0xFF161616)
val MonochromeDarkSurfaceElevated = Color(0xFF222222)
val MonochromeDarkPrimary = Color(0xFFFFFFFF)
val MonochromeDarkOnPrimary = Color(0xFF000000)
val MonochromeDarkTextPrimary = Color(0xFFFFFFFF)
val MonochromeDarkTextSecondary = Color(0xFFA0A0A0)
val MonochromeDarkBorder = Color(0xFF282828)
val MonochromeDarkBorderLight = Color(0xFF1A1A1A)

// Universal Accents (Sleek monochrome gradients)
val MonochromeGradientLight = Brush.linearGradient(
    colors = listOf(
        Color(0xFF000000),
        Color(0xFF2B2B2B),
        Color(0xFF444444)
    )
)

val MonochromeGradientDark = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFFFFFF),
        Color(0xFFCCCCCC),
        Color(0xFFAAAAAA)
    )
)

// Accent YouTube Red (Kept minimal for video indicator)
val YouTubeRed = Color(0xFFFF0000)
val ErrorRed = Color(0xFFE53935)
val SuccessGreen = Color(0xFF4CAF50)
