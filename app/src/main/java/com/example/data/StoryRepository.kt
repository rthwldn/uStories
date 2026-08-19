package com.example.data

import com.example.db.SavedStoryEntity
import com.example.db.StoryDao
import kotlinx.coroutines.flow.Flow

class StoryRepository(private val storyDao: StoryDao) {
    val allSavedStories: Flow<List<SavedStoryEntity>> = storyDao.getAllStories()

    suspend fun saveStory(story: SavedStoryEntity): Long {
        return storyDao.insertStory(story)
    }

    suspend fun getStoryById(id: Long): SavedStoryEntity? {
        return storyDao.getStoryById(id)
    }

    suspend fun deleteStory(story: SavedStoryEntity) {
        storyDao.deleteStory(story)
    }

    suspend fun deleteById(id: Long) {
        storyDao.deleteById(id)
    }
}
