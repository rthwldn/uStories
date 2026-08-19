package com.example.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryDao {
    @Query("SELECT * FROM saved_stories ORDER BY createdAt DESC")
    fun getAllStories(): Flow<List<SavedStoryEntity>>

    @Query("SELECT * FROM saved_stories WHERE id = :id")
    suspend fun getStoryById(id: Long): SavedStoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: SavedStoryEntity): Long

    @Delete
    suspend fun deleteStory(story: SavedStoryEntity)

    @Query("DELETE FROM saved_stories WHERE id = :id")
    suspend fun deleteById(id: Long)
}
