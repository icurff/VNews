package com.example.vnews.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "viewed_articles",
)
data class ViewedArticleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "article_id") val articleId: Int,
    @ColumnInfo(name = "viewed_at") val viewedAt: Long = System.currentTimeMillis() / 1000
) 