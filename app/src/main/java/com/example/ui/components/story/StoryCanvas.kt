package com.example.ui.components.story

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.StoryCustomization
import com.example.model.YouTubeVideo

@Composable
fun StoryCanvas(
    video: YouTubeVideo,
    customization: StoryCustomization,
    modifier: Modifier = Modifier
) {
    val template = customization.template
    val bgBrush = Brush.verticalGradient(template.gradientColors)

    // 9:16 Aspect Ratio Frame - Smooth and borderless
    Box(
        modifier = modifier
            .aspectRatio(9f / 16f)
            .shadow(16.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(bgBrush)
            .testTag("story_canvas_container"),
        contentAlignment = Alignment.Center
    ) {
        // If template uses blurred thumbnail background, render heavily blurred full-bleed thumbnail with dark scrim
        if (template.isBlurredThumbnailBg && video.thumbnailUrl.isNotBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(video.thumbnailUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(40.dp)
            )
            // Dark vignette/scrim overlay over blurred image for contrast
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.55f),
                                Color.Black.copy(alpha = 0.75f)
                            )
                        )
                    )
            )
        }

        // Centered Video Card - Floating naturally with soft shadow without harsh borders
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .shadow(18.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = template.cardBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // 16:9 Clean Video Thumbnail
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF101010)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(video.thumbnailUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = video.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Video Duration Badge on Bottom-Left (like YouTube)
                    if (video.formattedDuration.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.Black.copy(alpha = 0.85f),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                                .testTag("video_duration_badge")
                        ) {
                            Text(
                                text = video.formattedDuration,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Video Title
                Text(
                    text = video.title,
                    color = template.textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                // Channel Name + Views (NO upload date, joined with dot separator)
                val metaParts = mutableListOf<String>()
                if (video.channelTitle.isNotBlank()) {
                    metaParts.add(video.channelTitle)
                }
                if (video.viewCount.isNotBlank()) {
                    metaParts.add(video.viewCount)
                }

                if (metaParts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = metaParts.joinToString(" • "),
                        color = template.metaColor,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .testTag("video_meta_text")
                    )
                }
            }
        }
    }
}
