package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.db.SavedStoryEntity
import com.example.model.StoryTemplate
import com.example.ui.theme.ErrorRed
import com.example.viewmodel.AppScreen
import com.example.viewmodel.StoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedStoriesScreen(
    viewModel: StoryViewModel,
    savedStories: List<SavedStoryEntity>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val dateFormat = SimpleDateFormat("dd. MM. yyyy HH:mm", Locale.getDefault())

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Uložené koncepty Stories",
                            color = colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "${savedStories.size}",
                                color = colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        modifier = Modifier.testTag("saved_stories_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Zpět",
                            tint = colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        if (savedStories.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = colorScheme.surfaceVariant,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.BookmarkBorder,
                            contentDescription = null,
                            tint = colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Zatím žádné uložené Stories",
                        color = colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Vložte odkaz na video a vytvořte si 9:16 Instagram Story s barevným gradientem.",
                        color = colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Vložit odkaz",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(savedStories, key = { it.id }) { story ->
                    val template = StoryTemplate.fromId(story.templateId)

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, colorScheme.outline, RoundedCornerShape(16.dp))
                            .clickable {
                                viewModel.loadSavedStory(story)
                            }
                            .testTag("saved_story_item_${story.id}"),
                        shape = RoundedCornerShape(16.dp),
                        color = colorScheme.surface,
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 9:16 Mini Preview
                            Box(
                                modifier = Modifier
                                    .width(70.dp)
                                    .aspectRatio(9f / 16f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF141414)),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = story.thumbnailUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Surface(
                                    shape = CircleShape,
                                    color = Color.Black.copy(alpha = 0.6f),
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Details
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = "${template.emoji} ${template.title}",
                                            color = colorScheme.onSurfaceVariant,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = story.exportFormat,
                                            color = colorScheme.onSurfaceVariant,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = story.videoTitle,
                                    color = colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "${story.channelTitle} • ${story.viewCount}",
                                    color = colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = dateFormat.format(Date(story.createdAt)),
                                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    fontSize = 9.sp
                                )
                            }

                            // Action buttons
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                IconButton(
                                    onClick = { viewModel.loadSavedStory(story) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = "Upravit",
                                        tint = colorScheme.onSurface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        viewModel.deleteSavedStory(story.id)
                                        Toast.makeText(context, "Story byla smazána", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.DeleteOutline,
                                        contentDescription = "Smazat",
                                        tint = ErrorRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
