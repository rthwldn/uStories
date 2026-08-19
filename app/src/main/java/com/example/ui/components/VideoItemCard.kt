package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.YouTubeVideo

@Composable
fun VideoItemCard(
    video: YouTubeVideo,
    onSelect: (YouTubeVideo) -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    val colorScheme = MaterialTheme.colorScheme
    val borderColor = if (isSelected) colorScheme.primary else colorScheme.outline
    val backgroundColor = if (isSelected) colorScheme.surfaceVariant else colorScheme.surface

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onSelect(video) }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("video_item_${video.id}"),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        shadowElevation = if (isSelected) 3.dp else 1.dp
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail with duration tag & play icon
            Box(
                modifier = Modifier
                    .width(115.dp)
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF141414)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = video.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Play overlay
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.padding(4.dp)
                    )
                }

                // Duration badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.8f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                ) {
                    Text(
                        text = video.formattedDuration,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = video.title,
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = video.channelTitle,
                    color = colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = video.viewCount,
                        color = colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "•",
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontSize = 10.sp
                    )
                    Text(
                        text = video.publishedDate,
                        color = colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
