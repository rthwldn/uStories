package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExportFormat
import com.example.model.StoryTemplate
import com.example.ui.components.ExportShareDialog
import com.example.ui.components.TemplateCard
import com.example.ui.components.story.StoryCanvas
import com.example.util.StoryExportHelper
import com.example.viewmodel.StoryUiState
import com.example.viewmodel.StoryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: StoryViewModel,
    uiState: StoryUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val coroutineScope = rememberCoroutineScope()
    val video = uiState.selectedVideo
    val customization = uiState.customization

    var isSharing by remember { mutableStateOf(false) }

    if (uiState.showExportDialog) {
        ExportShareDialog(
            video = video,
            customization = customization,
            onDismiss = { viewModel.showExportDialog(false) },
            onStorySavedLocally = {
                viewModel.saveCurrentStoryToDb()
                Toast.makeText(context, "Story uložena do konceptů! 💾", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Centered 9:16 Canvas Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                StoryCanvas(
                    video = video,
                    customization = customization,
                    modifier = Modifier
                        .fillMaxWidth(0.70f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Customization Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = colorScheme.surface,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Templates Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Barevný gradient pozadí",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = colorScheme.onSurface
                        )
                        Text(
                            text = customization.template.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Template Gradient List
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(StoryTemplate.entries.toTypedArray()) { template ->
                            TemplateCard(
                                template = template,
                                isSelected = customization.template == template,
                                onSelect = { viewModel.selectTemplate(template) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Format Selection (PNG / JPG)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Formát exportu",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = colorScheme.onSurface
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = customization.exportFormat == ExportFormat.PNG,
                                onClick = { viewModel.setExportFormat(ExportFormat.PNG) },
                                label = { Text("PNG", fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = colorScheme.primary,
                                    selectedLabelColor = colorScheme.onPrimary,
                                    containerColor = colorScheme.surfaceVariant,
                                    labelColor = colorScheme.onSurfaceVariant
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = customization.exportFormat == ExportFormat.PNG,
                                    borderColor = if (customization.exportFormat == ExportFormat.PNG) colorScheme.primary else colorScheme.outline
                                )
                            )
                            FilterChip(
                                selected = customization.exportFormat == ExportFormat.JPG,
                                onClick = { viewModel.setExportFormat(ExportFormat.JPG) },
                                label = { Text("JPG", fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = colorScheme.primary,
                                    selectedLabelColor = colorScheme.onPrimary,
                                    containerColor = colorScheme.surfaceVariant,
                                    labelColor = colorScheme.onSurfaceVariant
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = customization.exportFormat == ExportFormat.JPG,
                                    borderColor = if (customization.exportFormat == ExportFormat.JPG) colorScheme.primary else colorScheme.outline
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Direct System Share Button ("Sdílet")
                    Button(
                        onClick = {
                            if (!isSharing) {
                                isSharing = true
                                coroutineScope.launch {
                                    val bitmap = StoryExportHelper.renderStoryBitmap(context, video, customization)
                                    viewModel.saveCurrentStoryToDb()
                                    StoryExportHelper.openSystemShareSheet(
                                        context = context,
                                        bitmap = bitmap,
                                        videoUrl = video.watchUrl,
                                        storyTitle = video.title,
                                        format = customization.exportFormat
                                    )
                                    isSharing = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .testTag("direct_share_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isSharing
                    ) {
                        if (isSharing) {
                            CircularProgressIndicator(
                                color = colorScheme.onPrimary,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp
                            )
                        } else {
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
                                    text = "Sdílet",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.onPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Export / Details Modal Button
                    OutlinedButton(
                        onClick = { viewModel.showExportDialog(true) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .testTag("open_export_sheet_button"),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outline),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colorScheme.onSurface
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Download,
                                contentDescription = null,
                                tint = colorScheme.onSurface
                            )
                            Text(
                                text = "Exportovat Story (${customization.exportFormat.name})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
