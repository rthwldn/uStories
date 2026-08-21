package com.example

import android.app.Application
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.core.app.ApplicationProvider
import com.example.db.SavedStoryEntity
import com.example.model.ExportFormat
import com.example.model.StoryCustomization
import com.example.model.StoryTemplate
import com.example.model.YouTubeVideo
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SavedStoriesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.StoryUiState
import com.example.viewmodel.StoryViewModel
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class AppScreenshotsTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val sampleVideo = YouTubeVideo(
      id = "dQw4w9WgXcQ",
      title = "Jak funguje umělá inteligence a tvorba obsahu v roce 2026",
      channelTitle = "TechTalks CZ",
      thumbnailUrl = "https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
      durationSeconds = 620,
      viewCount = "420 tis. zhlédnutí",
      publishedDate = "18. 8. 2026",
      category = "AI & Tech",
      description = "Rozhovor s předními vývojáři o budoucnosti AI v kreativním průmyslu."
  )

  @Test
  fun screenshot_01_home_screen() {
    val context = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = StoryViewModel(context)
    val state = StoryUiState(
        currentScreen = AppScreen.HOME,
        inputUrl = "https://youtu.be/dQw4w9WgXcQ"
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        HomeScreen(
            viewModel = viewModel,
            uiState = state,
            savedStoriesCount = 2
        )
      }
    }
    composeTestRule.waitForIdle()
    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/01_home_screen.png")
  }

  @Test
  fun screenshot_02_editor_screen() {
    val context = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = StoryViewModel(context)
    val state = StoryUiState(
        currentScreen = AppScreen.EDITOR,
        selectedVideo = sampleVideo,
        customization = StoryCustomization(
            template = StoryTemplate.INSTAGRAM_SUNSET,
            exportFormat = ExportFormat.PNG
        )
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        EditorScreen(
            viewModel = viewModel,
            uiState = state
        )
      }
    }
    composeTestRule.waitForIdle()
    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/02_editor_screen.png")
  }

  @Test
  fun screenshot_03_saved_stories_screen() {
    val context = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = StoryViewModel(context)
    val mockSavedList = listOf(
        SavedStoryEntity(
            id = 1,
            videoId = "dQw4w9WgXcQ",
            videoTitle = "Jak funguje umělá inteligence v roce 2026",
            channelTitle = "TechTalks CZ",
            thumbnailUrl = "https://img.youtube.com/vi/dQw4w9WgXcQ/hqdefault.jpg",
            templateId = StoryTemplate.DEEP_PURPLE.id,
            viewCount = "420 tis. zhlédnutí",
            publishedDate = "18. 8. 2026",
            createdAt = System.currentTimeMillis() - 3600000,
            exportFormat = "PNG"
        ),
        SavedStoryEntity(
            id = 2,
            videoId = "jNQXAC9IVRw",
            videoTitle = "První video v historii YouTube - San Diego Zoo",
            channelTitle = "jawed",
            thumbnailUrl = "https://img.youtube.com/vi/jNQXAC9IVRw/hqdefault.jpg",
            templateId = StoryTemplate.MIDNIGHT_BLUE.id,
            viewCount = "315 mil. zhlédnutí",
            publishedDate = "23. 4. 2005",
            createdAt = System.currentTimeMillis() - 86400000,
            exportFormat = "JPG"
        )
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        SavedStoriesScreen(
            viewModel = viewModel,
            savedStories = mockSavedList
        )
      }
    }
    composeTestRule.waitForIdle()
    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/03_saved_stories.png")
  }
}
