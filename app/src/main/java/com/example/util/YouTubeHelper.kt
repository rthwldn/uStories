package com.example.util

import com.example.model.YouTubeVideo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object YouTubeHelper {

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()
    }

    private val YOUTUBE_REGEX = Pattern.compile(
        "^.*(?:(?:youtu\\.be\\/|v\\/|vi\\/|u\\/\\w\\/|embed\\/|shorts\\/)|(?:(?:watch)?\\?v(?:i)?=|\\&v(?:i)?=))([^#\\&\\?]*).*",
        Pattern.CASE_INSENSITIVE
    )

    fun extractVideoId(input: String): String? {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return null

        // Check if user entered just the 11 character ID
        if (trimmed.matches(Regex("^[a-zA-Z0-9_-]{11}$"))) {
            return trimmed
        }

        val matcher = YOUTUBE_REGEX.matcher(trimmed)
        if (matcher.matches()) {
            val id = matcher.group(1)
            if (id != null && id.length == 11) {
                return id
            }
        }
        return null
    }

    fun getThumbnailUrl(videoId: String, quality: String = "maxres"): String {
        return when (quality) {
            "maxres" -> "https://img.youtube.com/vi/$videoId/maxresdefault.jpg"
            "hq" -> "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
            "mq" -> "https://img.youtube.com/vi/$videoId/mqdefault.jpg"
            else -> "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
        }
    }

    suspend fun fetchVideoInfo(videoId: String): YouTubeVideo = withContext(Dispatchers.IO) {
        var videoTitle = "YouTube Video"
        var authorName = "YouTube Tvůrce"
        var thumbUrl = getThumbnailUrl(videoId, "hq")
        var realViewCount = ""
        var publishedDate = ""

        // 1. Fetch metadata via YouTube oEmbed API
        try {
            val oEmbedUrl = "https://www.youtube.com/oembed?url=https://www.youtube.com/watch?v=$videoId&format=json"
            val request = Request.Builder()
                .url(oEmbedUrl)
                .header("User-Agent", "Mozilla/5.0 (Android)")
                .build()
            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string()
                if (!body.isNullOrEmpty()) {
                    val json = JSONObject(body)
                    videoTitle = json.optString("title", videoTitle)
                    authorName = json.optString("author_name", authorName)
                    thumbUrl = json.optString("thumbnail_url", thumbUrl)
                }
            }
        } catch (_: Exception) {
            // Continue to HTML scraping fallback
        }

        // 2. Fetch real viewCount and upload date from YouTube watch page HTML
        try {
            val watchUrl = "https://www.youtube.com/watch?v=$videoId"
            val pageRequest = Request.Builder()
                .url(watchUrl)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                .header("Accept-Language", "cs,en-US;q=0.9,en;q=0.8")
                .build()
            val pageResponse = httpClient.newCall(pageRequest).execute()
            if (pageResponse.isSuccessful) {
                val html = pageResponse.body?.string().orEmpty()

                // Check itemprop="interactionCount" content="123456"
                val interactionPattern = Pattern.compile("itemprop=\"interactionCount\"\\s+content=\"(\\d+)\"")
                val interactionMatcher = interactionPattern.matcher(html)
                if (interactionMatcher.find()) {
                    val count = interactionMatcher.group(1)?.toLongOrNull()
                    if (count != null && count > 0) {
                        realViewCount = formatCzechViewCount(count)
                    }
                }

                // Fallback check "viewCount":"123456" in JSON
                if (realViewCount.isEmpty()) {
                    val viewCountPattern = Pattern.compile("\"viewCount\"\\s*:\\s*\"(\\d+)\"")
                    val viewMatcher = viewCountPattern.matcher(html)
                    if (viewMatcher.find()) {
                        val count = viewMatcher.group(1)?.toLongOrNull()
                        if (count != null && count > 0) {
                            realViewCount = formatCzechViewCount(count)
                        }
                    }
                }

                // Check published date: itemprop="datePublished" content="2024-01-15"
                val datePattern = Pattern.compile("itemprop=\"datePublished\"\\s+content=\"([^\"]+)\"")
                val dateMatcher = datePattern.matcher(html)
                if (dateMatcher.find()) {
                    val rawDate = dateMatcher.group(1).orEmpty()
                    publishedDate = formatCzechDate(rawDate)
                }
            }
        } catch (_: Exception) {
            // If fetching failed, realViewCount remains empty as requested
        }

        YouTubeVideo(
            id = videoId,
            title = videoTitle,
            channelTitle = authorName,
            thumbnailUrl = thumbUrl,
            durationSeconds = 0,
            viewCount = realViewCount,
            publishedDate = publishedDate,
            category = "Importováno",
            description = "YouTube video z odkazu."
        )
    }

    private fun formatCzechViewCount(count: Long): String {
        return when {
            count >= 1_000_000_000 -> {
                val formatted = String.format(Locale.getDefault(), "%.1f", count / 1_000_000_000.0)
                "$formatted mld. zhlédnutí"
            }
            count >= 1_000_000 -> {
                val formatted = String.format(Locale.getDefault(), "%.1f", count / 1_000_000.0)
                "$formatted mil. zhlédnutí"
            }
            count >= 1_000 -> {
                val formatted = NumberFormat.getInstance(Locale("cs", "CZ")).format(count / 1_000)
                "$formatted tis. zhlédnutí"
            }
            count > 0 -> {
                val formatted = NumberFormat.getInstance(Locale("cs", "CZ")).format(count)
                "$formatted zhlédnutí"
            }
            else -> ""
        }
    }

    private fun formatCzechDate(rawDate: String): String {
        if (rawDate.isBlank()) return ""

        // Extract YYYY-MM-DD from strings like "2026-08-18T06:06:39.07:00" or "2024-03-15"
        val isoDateRegex = Regex("""(\d{4})-(\d{1,2})-(\d{1,2})""")
        val match = isoDateRegex.find(rawDate)
        if (match != null) {
            val (year, monthStr, dayStr) = match.destructured
            val month = monthStr.toIntOrNull() ?: 1
            val day = dayStr.toIntOrNull() ?: 1
            return "$day. $month. $year"
        }

        return rawDate.trim()
    }

    val SAMPLE_VIDEOS: List<YouTubeVideo> = listOf(
        YouTubeVideo(
            id = "dQw4w9WgXcQ",
            title = "Never Gonna Give You Up (Official Music Video 4K)",
            channelTitle = "Rick Astley",
            thumbnailUrl = "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
            durationSeconds = 213,
            viewCount = "1,5 mld. zhlédnutí",
            publishedDate = "25. 10. 2009",
            category = "Hudba",
            description = "Ikonický videoklip v remasterované 4K kvalitě."
        )
    )
}
