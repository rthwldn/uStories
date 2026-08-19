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
    val metaColor: Color
) {
    DARK_MINIMAL(
        id = "dark_minimal",
        title = "Tmavý gradient",
        emoji = "🌑",
        gradientColors = listOf(Color(0xFF0D0B14), Color(0xFF1B1626), Color(0xFF0F0D18)),
        gradientIntColors = intArrayOf(
            AndroidColor.parseColor("#0D0B14"),
            AndroidColor.parseColor("#1B1626"),
            AndroidColor.parseColor("#0F0D18")
        ),
        cardBackground = Color(0xFF1E1A2B),
        cardBackgroundInt = AndroidColor.parseColor("#1E1A2B"),
        cardBorderColor = Color(0x33FFFFFF),
        cardBorderColorInt = AndroidColor.parseColor("#33FFFFFF"),
        textColor = Color(0xFFFFFFFF),
        metaColor = Color(0xFFB3B0C2)
    ),
    MIDNIGHT_BLUE(
        id = "midnight_blue",
        title = "Půlnoční modrá",
        emoji = "🌌",
        gradientColors = listOf(Color(0xFF0A1128), Color(0xFF1C2541), Color(0xFF0B132B)),
        gradientIntColors = intArrayOf(
            AndroidColor.parseColor("#0A1128"),
            AndroidColor.parseColor("#1C2541"),
            AndroidColor.parseColor("#0B132B")
        ),
        cardBackground = Color(0xFF14213D),
        cardBackgroundInt = AndroidColor.parseColor("#14213D"),
        cardBorderColor = Color(0x3364DFDF),
        cardBorderColorInt = AndroidColor.parseColor("#3364DFDF"),
        textColor = Color(0xFFFFFFFF),
        metaColor = Color(0xFF90E0EF)
    ),
    INSTAGRAM_SUNSET(
        id = "instagram_sunset",
        title = "Instagram Sunset",
        emoji = "🌅",
        gradientColors = listOf(Color(0xFF4C1D95), Color(0xFF831843), Color(0xFF7C2D12)),
        gradientIntColors = intArrayOf(
            AndroidColor.parseColor("#4C1D95"),
            AndroidColor.parseColor("#831843"),
            AndroidColor.parseColor("#7C2D12")
        ),
        cardBackground = Color(0xFF2E1065).copy(alpha = 0.95f),
        cardBackgroundInt = AndroidColor.parseColor("#2E1065"),
        cardBorderColor = Color(0x44F43F5E),
        cardBorderColorInt = AndroidColor.parseColor("#44F43F5E"),
        textColor = Color(0xFFFFFFFF),
        metaColor = Color(0xFFFDE047)
    ),
    DEEP_PURPLE(
        id = "deep_purple",
        title = "Temně fialová",
        emoji = "🔮",
        gradientColors = listOf(Color(0xFF1E1035), Color(0xFF381E72), Color(0xFF190C2F)),
        gradientIntColors = intArrayOf(
            AndroidColor.parseColor("#1E1035"),
            AndroidColor.parseColor("#381E72"),
            AndroidColor.parseColor("#190C2F")
        ),
        cardBackground = Color(0xFF261546),
        cardBackgroundInt = AndroidColor.parseColor("#261546"),
        cardBorderColor = Color(0x44D0BCFF),
        cardBorderColorInt = AndroidColor.parseColor("#44D0BCFF"),
        textColor = Color(0xFFFFFFFF),
        metaColor = Color(0xFFEADDFF)
    ),
    EMERALD_NIGHT(
        id = "emerald_night",
        title = "Smaragdová",
        emoji = "🌲",
        gradientColors = listOf(Color(0xFF042F2E), Color(0xFF0D4A46), Color(0xFF032221)),
        gradientIntColors = intArrayOf(
            AndroidColor.parseColor("#042F2E"),
            AndroidColor.parseColor("#0D4A46"),
            AndroidColor.parseColor("#032221")
        ),
        cardBackground = Color(0xFF0A3633),
        cardBackgroundInt = AndroidColor.parseColor("#0A3633"),
        cardBorderColor = Color(0x332DD4BF),
        cardBorderColorInt = AndroidColor.parseColor("#332DD4BF"),
        textColor = Color(0xFFFFFFFF),
        metaColor = Color(0xFF99F6E4)
    ),
    CRIMSON_DARK(
        id = "crimson_dark",
        title = "Karmínová",
        emoji = "🔥",
        gradientColors = listOf(Color(0xFF28090C), Color(0xFF4A1016), Color(0xFF1C0507)),
        gradientIntColors = intArrayOf(
            AndroidColor.parseColor("#28090C"),
            AndroidColor.parseColor("#4A1016"),
            AndroidColor.parseColor("#1C0507")
        ),
        cardBackground = Color(0xFF380C11),
        cardBackgroundInt = AndroidColor.parseColor("#380C11"),
        cardBorderColor = Color(0x33F87171),
        cardBorderColorInt = AndroidColor.parseColor("#33F87171"),
        textColor = Color(0xFFFFFFFF),
        metaColor = Color(0xFFFECACA)
    ),
    CHARCOAL_SLATE(
        id = "charcoal_slate",
        title = "Uhlíkový minimal",
        emoji = "🖤",
        gradientColors = listOf(Color(0xFF000000), Color(0xFF18181B), Color(0xFF09090B)),
        gradientIntColors = intArrayOf(
            AndroidColor.parseColor("#000000"),
            AndroidColor.parseColor("#18181B"),
            AndroidColor.parseColor("#09090B")
        ),
        cardBackground = Color(0xFF18181B),
        cardBackgroundInt = AndroidColor.parseColor("#18181B"),
        cardBorderColor = Color(0x2EFFFFFF),
        cardBorderColorInt = AndroidColor.parseColor("#2EFFFFFF"),
        textColor = Color(0xFFFFFFFF),
        metaColor = Color(0xFFA1A1AA)
    ),
    OCEAN_CYAN(
        id = "ocean_cyan",
        title = "Oceánská",
        emoji = "🌊",
        gradientColors = listOf(Color(0xFF081C2C), Color(0xFF0F3A5A), Color(0xFF05131E)),
        gradientIntColors = intArrayOf(
            AndroidColor.parseColor("#081C2C"),
            AndroidColor.parseColor("#0F3A5A"),
            AndroidColor.parseColor("#05131E")
        ),
        cardBackground = Color(0xFF0C2B44),
        cardBackgroundInt = AndroidColor.parseColor("#0C2B44"),
        cardBorderColor = Color(0x3338BDF8),
        cardBorderColorInt = AndroidColor.parseColor("#3338BDF8"),
        textColor = Color(0xFFFFFFFF),
        metaColor = Color(0xFFBAE6FD)
    );

    companion object {
        fun fromId(id: String): StoryTemplate {
            return entries.find { it.id == id } ?: DARK_MINIMAL
        }
    }
}
