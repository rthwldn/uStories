package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SavedStoriesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.StoryViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                UStoriesApp()
            }
        }
    }
}

@Composable
fun UStoriesApp(
    viewModel: StoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val savedStories by viewModel.savedStories.collectAsStateWithLifecycle()

    // Handle system back button smoothly
    BackHandler(enabled = uiState.currentScreen != AppScreen.HOME) {
        viewModel.navigateTo(AppScreen.HOME)
    }

    AnimatedContent(
        targetState = uiState.currentScreen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "screen_transition",
        modifier = Modifier.fillMaxSize()
    ) { screen ->
        when (screen) {
            AppScreen.HOME -> {
                HomeScreen(
                    viewModel = viewModel,
                    uiState = uiState,
                    savedStoriesCount = savedStories.size
                )
            }
            AppScreen.EDITOR -> {
                EditorScreen(
                    viewModel = viewModel,
                    uiState = uiState
                )
            }
            AppScreen.SAVED_STORIES -> {
                SavedStoriesScreen(
                    viewModel = viewModel,
                    savedStories = savedStories
                )
            }
        }
    }
}
