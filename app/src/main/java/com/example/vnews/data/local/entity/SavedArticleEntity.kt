package com.example.vnews.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "saved_articles",
)
data class SavedArticleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "article_id") val articleId: Int,
    @ColumnInfo(name = "saved_at") val savedAt: Long = System.currentTimeMillis() / 1000
)