package com.example.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_stories")
data class SavedStoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val videoId: String,
    val videoTitle: String,
    val channelTitle: String,
    val thumbnailUrl: String,
    val templateId: String,
    val viewCount: String = "",
    val publishedDate: String = "",
    val exportFormat: String = "PNG",
    val createdAt: Long = System.currentTimeMillis()
)
