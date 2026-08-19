package com.example.ui.screens

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ErrorRed
import com.example.viewmodel.AppScreen
import com.example.viewmodel.StoryUiState
import com.example.viewmodel.StoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: StoryViewModel,
    uiState: StoryUiState,
    savedStoriesCount: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "uStories",
                            color = colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "YouTube do 9:16 Story",
                            color = colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                },
                actions = {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { viewModel.navigateTo(AppScreen.SAVED_STORIES) }
                            .border(1.dp, colorScheme.outline, RoundedCornerShape(14.dp))
                            .testTag("nav_saved_stories_button"),
                        color = colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Bookmark,
                                contentDescription = "Uložené",
                                tint = colorScheme.onBackground,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (savedStoriesCount > 0) "Uložené ($savedStoriesCount)" else "Uložené",
                                color = colorScheme.onBackground,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        // Direct Centered Input Layout
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Centered Hero Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, colorScheme.outline, RoundedCornerShape(24.dp))
                        .testTag("hero_input_container"),
                    color = colorScheme.surface,
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Badge Icon
                        Surface(
                            shape = CircleShape,
                            color = colorScheme.primary,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.PlayCircle,
                                    contentDescription = null,
                                    tint = colorScheme.onPrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Vložte odkaz na video",
                            color = colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Zadejte YouTube odkaz nebo Shorts pro automatické vygenerování 9:16 karty pro Instagram Story.",
                            color = colorScheme.onSurfaceVariant,
                            fontSize = 12.5.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // URL Input field
                        OutlinedTextField(
                            value = uiState.inputUrl,
                            onValueChange = { viewModel.setInputUrl(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("youtube_url_input"),
                            placeholder = {
                                Text(
                                    text = "https://youtube.com/watch?v=...",
                                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    fontSize = 13.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Link,
                                    contentDescription = null,
                                    tint = colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Uri,
                                imeAction = ImeAction.Go
                            ),
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    if (uiState.inputUrl.isNotBlank()) {
                                        viewModel.loadVideoFromUrl(uiState.inputUrl)
                                    }
                                }
                            ),
                            trailingIcon = {
                                if (uiState.inputUrl.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setInputUrl("") }) {
                                        Icon(
                                            imageVector = Icons.Rounded.Clear,
                                            contentDescription = "Smazat",
                                            tint = colorScheme.onSurfaceVariant
                                        )
                                    }
                                } else {
                                    // Paste from clipboard
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val item = clipboard.primaryClip?.getItemAt(0)
                                            val text = item?.text?.toString() ?: ""
                                            if (text.isNotBlank()) {
                                                viewModel.setInputUrl(text)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.ContentPaste,
                                            contentDescription = "Vložit ze schránky",
                                            tint = colorScheme.onBackground
                                        )
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorScheme.primary,
                                unfocusedBorderColor = colorScheme.outline,
                                focusedTextColor = colorScheme.onSurface,
                                unfocusedTextColor = colorScheme.onSurface,
                                focusedContainerColor = colorScheme.surfaceVariant,
                                unfocusedContainerColor = colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )

                        // Error message
                        if (uiState.errorMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.errorMessage,
                                color = ErrorRed,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                if (uiState.inputUrl.isNotBlank()) {
                                    viewModel.loadVideoFromUrl(uiState.inputUrl)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("load_video_button"),
                            enabled = uiState.inputUrl.isNotBlank() && !uiState.isLoadingVideo,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.primary,
                                contentColor = colorScheme.onPrimary,
                                disabledContainerColor = colorScheme.surfaceVariant,
                                disabledContentColor = colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                        ) {
                            if (uiState.isLoadingVideo) {
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
                                    Text(
                                        text = "Pokračovat k vytvoření",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.5.sp
                                    )
                                    Icon(
                                        imageVector = Icons.Rounded.ArrowForward,
                                        contentDescription = null,
                                        tint = colorScheme.onPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                if (savedStoriesCount > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.navigateTo(AppScreen.SAVED_STORIES) }
                            .border(1.dp, colorScheme.outline, RoundedCornerShape(12.dp)),
                        color = colorScheme.surface,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Bookmark,
                                contentDescription = null,
                                tint = colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Máte $savedStoriesCount uložených konceptů",
                                color = colorScheme.onSurfaceVariant,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
