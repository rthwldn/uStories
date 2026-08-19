package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.StoryTemplate

@Composable
fun TemplateCard(
    template: StoryTemplate,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val borderColor = if (isSelected) colorScheme.primary else colorScheme.outline
    val borderWidth = if (isSelected) 2.dp else 1.dp
    val backgroundColor = if (isSelected) colorScheme.surfaceVariant else colorScheme.surface

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onSelect() }
            .border(borderWidth, borderColor, RoundedCornerShape(16.dp))
            .testTag("template_card_${template.id}"),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        tonalElevation = if (isSelected) 4.dp else 1.dp
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .width(110.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Gradient Preview Frame (9:16 silhouette)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(86.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.verticalGradient(template.gradientColors))
                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                // Mini Centered Card representation
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.82f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(template.cardBackground)
                        .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "▶",
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }

                if (isSelected) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Selected",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Template Title
            Text(
                text = template.title,
                color = colorScheme.onSurface,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}
