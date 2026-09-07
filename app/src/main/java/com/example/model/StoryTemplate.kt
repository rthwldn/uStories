package com.example.model

import androidx.compose.ui.graphics.Color
import android.graphics.Color as AndroidColor

enum class StoryTemplate(
    val id: String,
    val title: String,
    val emoji: String,
    val gradientColors: List<Color>,
    val gradientIntColors: IntArray,
    val cardBackground: Color,
    val cardBackgroundInt: Int,
    val cardBorderColor: Color,
    val cardBorderColorInt: Int,
    val textColor: Color,
    val metaColor: Color,
    val isBlurredThumbnailBg: Boolean = false
) {
    CHARCOAL_SLATE(
        id = "charcoal_slate",
        title = "Minimal",
        emoji = "🖤",
        gradientColors = listOf(Color(0xFF222226), Color(0xFF0A0A0C)),
        gradientIntColors = intArrayOf(
            AndroidColor.parseColor("#222226"),
            AndroidColor.parseColor("#0A0A0C")
        ),
        cardBackground = Color(0xFF161618),
        cardBackgroundInt = AndroidColor.parseColor("#161618"),
        cardBorderColor = Color.Transparent,
        cardBorderColorInt = AndroidColor.TRANSPARENT,
        textColor = Color(0xFFFFFFFF),
        metaColor = Color(0xFFA1A1AA)
    ),
    LIGHT_MINIMAL(
        id = "light_minimal",
        title = "Světlý minimal",
        emoji = "🤍",
        gradientColors = listOf(Color(0xFFF8F9FA), Color(0xFFE9ECEF)),
        gradientIntColors = intArrayOf(
            AndroidColor.parseColor("#F8F9FA"),
            AndroidColor.parseColor("#E9ECEF")
        ),
        cardBackground = Color(0xFFFFFFFF),
        cardBackgroundInt = AndroidColor.parseColor("#FFFFFF"),
        cardBorderColor = Color.Transparent,
        cardBorderColorInt = AndroidColor.TRANSPARENT,
        textColor = Color(0xFF111827),
        metaColor = Color(0xFF6B7280)
    ),
    BLURRED_THUMBNAIL(
        id = "blurred_thumbnail",
        title = "Rozmazané video",
        emoji = "✨",
        gradientColors = listOf(Color(0xFF1C1A24), Color(0xFF0B0A0F)),
        gradientIntColors = intArrayOf(
            AndroidColor.parseColor("#1C1A24"),
            AndroidColor.parseColor("#0B0A0F")
        ),
        cardBackground = Color(0xE616141D),
        cardBackgroundInt = AndroidColor.parseColor("#E616141D"),
        cardBorderColor = Color.Transparent,
        cardBorderColorInt = AndroidColor.TRANSPARENT,
        textColor = Color(0xFFFFFFFF),
        metaColor = Color(0xFFD1D5DB),
        isBlurredThumbnailBg = true
    ),
    DARK_MINIMAL(
        id = "dark_minimal",
        title = "Tmavý gradient",
        emoji = "🌑",
        gradientColors = listOf(Color(0xFF1A1628), Color(0xFF090810)),
        gradientIntColors = intArrayOf(
            AndroidColor.parseColor("#1A1628"),
            AndroidColor.parseColor("#090810")
        ),
        cardBackground = Color(0xFF1E1A2B),
        cardBackgroundInt = AndroidColor.parseColor("#1E1A2B"),
        cardBorderColor = Color.Transparent,
        cardBorderColorInt = AndroidColor.TRANSPARENT,
        textColor = Color(0xFFFFFFFF),
        metaColor = Color(0xFFB3B0C2)
    ),
    MIDNIGHT_BLUE(
        id = "midnight_blue",
        title = "Půlnoční modrá",
        emoji = "🌌",
        gradientColors = listOf(Color(0xFF1C2847), Color(0xFF080D1A)),
        gradientIntColors = intArrayOf(
            AndroidColor.parseColor("#1C2847"),
            AndroidColor.parseColor("#080D1A")
        ),
        cardBackground = Color(0xFF142038),
        cardBackgroundInt = AndroidColor.parseColor("#142038"),
        cardBorderColor = Color.Transparent,
        cardBorderColorInt = AndroidColor.TRANSPARENT,
        textColor = Color(0xFFFFFFFF),
        metaColor = Color(0xFF90E0EF)
    ),
    INSTAGRAM_SUNSET(
        id = "instagram_sunset",
        title = "Instagram Sunset",
        emoji = "🌅",
        gradientColors = listOf(Color(0xFF831843), Color(0xFF310E54)),
        gradientIntColors = intArrayOf(
            AndroidColor.parseColor("#831843"),
            AndroidColor.parseColor("#310E54")
        ),
        cardBackground = Color(0xFF260D40),
        cardBackgroundInt = AndroidColor.parseColor("#260D40"),
        cardBorderColor = Color.Transparent,
        cardBorderColorInt = AndroidColor.TRANSPARENT,
        textColor = Color(0xFFFFFFFF),
        metaColor = Color(0xFFFDE047)
    ),
    EMERALD_NIGHT(
        id = "emerald_night",
        title = "Smaragdová",
        emoji = "🌲",
        gradientColors = listOf(Color(0xFF0D403B), Color(0xFF021716)),
        gradientIntColors = intArrayOf(
            AndroidColor.parseColor("#0D403B"),
            AndroidColor.parseColor("#021716")
        ),
        cardBackground = Color(0xFF0A2E2A),
        cardBackgroundInt = AndroidColor.parseColor("#0A2E2A"),
        cardBorderColor = Color.Transparent,
        cardBorderColorInt = AndroidColor.TRANSPARENT,
        textColor = Color(0xFFFFFFFF),
        metaColor = Color(0xFF99F6E4)
    ),
    CRIMSON_DARK(
        id = "crimson_dark",
        title = "Karmínová",
        emoji = "🔥",
        gradientColors = listOf(Color(0xFF4A1218), Color(0xFF150406)),
        gradientIntColors = intArrayOf(
            AndroidColor.parseColor("#4A1218"),
            AndroidColor.parseColor("#150406")
        ),
        cardBackground = Color(0xFF330C11),
        cardBackgroundInt = AndroidColor.parseColor("#330C11"),
        cardBorderColor = Color.Transparent,
        cardBorderColorInt = AndroidColor.TRANSPARENT,
        textColor = Color(0xFFFFFFFF),
        metaColor = Color(0xFFFECACA)
    );

    companion object {
        fun fromId(id: String): StoryTemplate {
            return entries.find { it.id == id } ?: CHARCOAL_SLATE
        }
    }
}
