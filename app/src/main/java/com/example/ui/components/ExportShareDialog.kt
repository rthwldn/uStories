package com.example.ui.components

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExportFormat
import com.example.model.StoryCustomization
import com.example.model.YouTubeVideo
import com.example.ui.theme.SuccessGreen
import com.example.util.StoryExportHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportShareDialog(
    video: YouTubeVideo,
    customization: StoryCustomization,
    onDismiss: () -> Unit,
    onStorySavedLocally: () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var currentFormat by remember { mutableStateOf(customization.exportFormat) }
    var renderedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isRendering by remember { mutableStateOf(true) }
    var isSavedToGallery by remember { mutableStateOf(false) }

    LaunchedEffect(video, customization) {
        isRendering = true
        val bmp = StoryExportHelper.renderStoryBitmap(context, video, customization)
        renderedBitmap = bmp
        isRendering = false
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Exportovat Story 9:16",
                        color = colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "1080 × 1920 • ${customization.template.title}",
                        color = colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Zavřít",
                        tint = colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rendered Preview Card
            Box(
                modifier = Modifier
                    .width(155.dp)
                    .aspectRatio(9f / 16f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF141414))
                    .border(1.5.dp, colorScheme.outline, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (isRendering || renderedBitmap == null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Načítání grafiky...",
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                } else {
                    Image(
                        bitmap = renderedBitmap!!.asImageBitmap(),
                        contentDescription = "Rendered Story Card",
                        modifier = Modifier.clip(RoundedCornerShape(18.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Format Selector in Dialog
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Formát: ",
                    color = colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(6.dp))
                FilterChip(
                    selected = currentFormat == ExportFormat.PNG,
                    onClick = { currentFormat = ExportFormat.PNG },
                    label = { Text("PNG", fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colorScheme.primary,
                        selectedLabelColor = colorScheme.onPrimary,
                        containerColor = colorScheme.surfaceVariant,
                        labelColor = colorScheme.onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = currentFormat == ExportFormat.PNG,
                        borderColor = if (currentFormat == ExportFormat.PNG) colorScheme.primary else colorScheme.outline
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = currentFormat == ExportFormat.JPG,
                    onClick = { currentFormat = ExportFormat.JPG },
                    label = { Text("JPG", fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colorScheme.primary,
                        selectedLabelColor = colorScheme.onPrimary,
                        containerColor = colorScheme.surfaceVariant,
                        labelColor = colorScheme.onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = currentFormat == ExportFormat.JPG,
                        borderColor = if (currentFormat == ExportFormat.JPG) colorScheme.primary else colorScheme.outline
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Action: Share to Instagram Story
            Button(
                onClick = {
                    renderedBitmap?.let { bmp ->
                        coroutineScope.launch {
                            StoryExportHelper.shareStoryToInstagramOrOthers(
                                context = context,
                                bitmap = bmp,
                                videoUrl = video.watchUrl,
                                storyTitle = video.title,
                                format = currentFormat
                            )
                            onStorySavedLocally()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .testTag("share_instagram_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = null,
                        tint = colorScheme.onPrimary
                    )
                    Text(
                        text = "Sdílet do Instagram Stories (${currentFormat.name})",
                        color = colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Secondary Action: Save to Gallery
            Button(
                onClick = {
                    renderedBitmap?.let { bmp ->
                        coroutineScope.launch {
                            val uri = StoryExportHelper.saveStoryToGallery(
                                context = context,
                                bitmap = bmp,
                                format = currentFormat,
                                title = "Story_${video.id}_${System.currentTimeMillis()}"
                            )
                            if (uri != null) {
                                isSavedToGallery = true
                                Toast.makeText(context, "Story uložena do galerie (${currentFormat.name})! 📸", Toast.LENGTH_LONG).show()
                                onStorySavedLocally()
                            } else {
                                Toast.makeText(context, "Story byla uložena do konceptů!", Toast.LENGTH_SHORT).show()
                                onStorySavedLocally()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_gallery_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.surfaceVariant,
                    contentColor = colorScheme.onSurface
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isSavedToGallery) Icons.Rounded.CheckCircle else Icons.Filled.Download,
                        contentDescription = null,
                        tint = if (isSavedToGallery) SuccessGreen else colorScheme.onSurface
                    )
                    Text(
                        text = if (isSavedToGallery) "Uloženo v galerii (${currentFormat.name}) ✅" else "Uložit obrázek do Galerie (${currentFormat.name})",
                        color = colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Utility Actions: Copy Link & Open on YouTube
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            StoryExportHelper.copyToClipboard(
                                context,
                                "YouTube Link",
                                video.watchUrl
                            )
                        }
                        .border(1.dp, colorScheme.outline, RoundedCornerShape(12.dp)),
                    color = colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = "Copy",
                            tint = colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Kopírovat link",
                            color = colorScheme.onSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            StoryExportHelper.openInYouTube(context, video.id)
                        }
                        .border(1.dp, colorScheme.outline, RoundedCornerShape(12.dp)),
                    color = colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                            contentDescription = "Open",
                            tint = Color(0xFFFF0000),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "YouTube Video",
                            color = colorScheme.onSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "💡 Tip pro Instagram: Vložte zkopírovaný odkaz do nálepky ODKAZ (Link Sticker) ve vašem Instagram příběhu.",
                color = colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                lineHeight = 15.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}
