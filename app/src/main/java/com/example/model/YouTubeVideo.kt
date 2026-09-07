package com.example.model

import java.util.Locale

data class YouTubeVideo(
    val id: String,
    val title: String,
    val channelTitle: String,
    val thumbnailUrl: String,
    val durationSeconds: Int = 0,
    val viewCount: String = "",
    val publishedDate: String = "",
    val category: String = "Vše",
    val description: String = ""
) {
    val watchUrl: String
        get() = "https://www.youtube.com/watch?v=$id"

    val shortUrl: String
        get() = "https://youtu.be/$id"

    val formattedDuration: String
        get() {
            if (durationSeconds <= 0) return ""
            val hours = durationSeconds / 3600
            val minutes = (durationSeconds % 3600) / 60
            val seconds = durationSeconds % 60
            return if (hours > 0) {
                String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
            }
        }

    val cleanPublishedDate: String
        get() {
            if (publishedDate.isBlank()) return ""
            val isoMatch = Regex("""(\d{4})-(\d{1,2})-(\d{1,2})""").find(publishedDate)
            if (isoMatch != null) {
                val (year, monthStr, dayStr) = isoMatch.destructured
                val month = monthStr.toIntOrNull() ?: 1
                val day = dayStr.toIntOrNull() ?: 1
                return "$day. $month. $year"
            }
            return publishedDate.trim()
        }
}
