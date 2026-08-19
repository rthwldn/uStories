package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.StoryRepository
import com.example.db.SavedStoryEntity
import com.example.db.StoryDatabase
import com.example.model.ExportFormat
import com.example.model.StoryCustomization
import com.example.model.StoryTemplate
import com.example.model.YouTubeVideo
import com.example.util.YouTubeHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    EDITOR,
    SAVED_STORIES
}

data class StoryUiState(
    val currentScreen: AppScreen = AppScreen.HOME,
    val selectedVideo: YouTubeVideo = YouTubeHelper.SAMPLE_VIDEOS.first(),
    val customization: StoryCustomization = StoryCustomization(),
    val inputUrl: String = "",
    val isLoadingVideo: Boolean = false,
    val errorMessage: String? = null,
    val showExportDialog: Boolean = false
)

class StoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StoryRepository

    private val _uiState = MutableStateFlow(StoryUiState())
    val uiState: StateFlow<StoryUiState> = _uiState.asStateFlow()

    val savedStories: StateFlow<List<SavedStoryEntity>>

    init {
        val db = StoryDatabase.getInstance(application)
        repository = StoryRepository(db.storyDao())

        savedStories = repository.allSavedStories.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.update { it.copy(currentScreen = screen, errorMessage = null) }
    }

    fun setInputUrl(url: String) {
        _uiState.update { it.copy(inputUrl = url, errorMessage = null) }
    }

    fun selectVideo(video: YouTubeVideo) {
        _uiState.update {
            it.copy(
                selectedVideo = video,
                currentScreen = AppScreen.EDITOR
            )
        }
    }

    fun loadVideoFromUrl(url: String) {
        val videoId = YouTubeHelper.extractVideoId(url)
        if (videoId == null) {
            _uiState.update { it.copy(errorMessage = "Neplatný odkaz na YouTube. Vložte např. https://youtu.be/... nebo https://youtube.com/watch?v=...") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingVideo = true, errorMessage = null) }
            val video = YouTubeHelper.fetchVideoInfo(videoId)
            _uiState.update {
                it.copy(
                    selectedVideo = video,
                    isLoadingVideo = false,
                    inputUrl = "",
                    currentScreen = AppScreen.EDITOR
                )
            }
        }
    }

    fun selectTemplate(template: StoryTemplate) {
        _uiState.update {
            it.copy(
                customization = it.customization.copy(
                    template = template
                )
            )
        }
    }

    fun setExportFormat(format: ExportFormat) {
        _uiState.update {
            it.copy(
                customization = it.customization.copy(
                    exportFormat = format
                )
            )
        }
    }

    fun showExportDialog(show: Boolean) {
        _uiState.update { it.copy(showExportDialog = show) }
    }

    fun saveCurrentStoryToDb() {
        val state = _uiState.value
        val video = state.selectedVideo
        val custom = state.customization

        val entity = SavedStoryEntity(
            videoId = video.id,
            videoTitle = video.title,
            channelTitle = video.channelTitle,
            thumbnailUrl = video.thumbnailUrl,
            templateId = custom.template.id,
            viewCount = video.viewCount,
            publishedDate = video.publishedDate,
            exportFormat = custom.exportFormat.name
        )

        viewModelScope.launch {
            repository.saveStory(entity)
        }
    }

    fun loadSavedStory(savedStory: SavedStoryEntity) {
        val template = StoryTemplate.fromId(savedStory.templateId)
        val format = try {
            ExportFormat.valueOf(savedStory.exportFormat)
        } catch (_: Exception) {
            ExportFormat.PNG
        }

        val video = YouTubeVideo(
            id = savedStory.videoId,
            title = savedStory.videoTitle,
            channelTitle = savedStory.channelTitle,
            thumbnailUrl = savedStory.thumbnailUrl,
            viewCount = if (savedStory.viewCount.isNotEmpty()) savedStory.viewCount else "1.2M zhlédnutí",
            publishedDate = if (savedStory.publishedDate.isNotEmpty()) savedStory.publishedDate else "Nahráno"
        )

        val custom = StoryCustomization(
            template = template,
            exportFormat = format
        )

        _uiState.update {
            it.copy(
                selectedVideo = video,
                customization = custom,
                currentScreen = AppScreen.EDITOR
            )
        }
    }

    fun deleteSavedStory(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }
}
