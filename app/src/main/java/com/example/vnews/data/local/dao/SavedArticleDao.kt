package com.example.vnews.data.local.dao

import androidx.room.*
import com.example.vnews.data.local.entity.ArticleEntity
import com.example.vnews.data.local.entity.SavedArticleEntity
import com.example.vnews.data.local.entity.ViewedArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun markArticleAsSaved(article: SavedArticleEntity)

    @Query("DELETE FROM saved_articles WHERE article_id = :articleId")
    suspend fun deleteSavedArticleById(articleId: Int)


    @Query("SELECT EXISTS(SELECT 1 FROM saved_articles WHERE article_id = :articleId)")
    fun isArticleSaved(articleId: Int): Flow<Boolean>
} 