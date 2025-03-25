package com.example.vnews.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.vnews.data.local.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface ArticleDao {


    @Transaction
    @Query(
        """
        SELECT a.* FROM articles a
        INNER JOIN viewed_articles va ON a.id = va.article_id
        ORDER BY va.viewed_at DESC
    """
    )
    fun getViewedArticles(): Flow<List<ArticleEntity>>

    @Transaction
    @Query(
        """
        SELECT a.* FROM articles a
        INNER JOIN saved_articles sa ON a.id = sa.article_id
        ORDER BY sa.saved_at DESC
    """
    )
    fun getSavedArticles(): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleEntity): Long

    @Query("SELECT id FROM articles WHERE source = :sourceUrl LIMIT 1")
    suspend fun getArticleIdBySource(sourceUrl: String): Int?

    @Query("SELECT EXISTS(SELECT 1 FROM saved_articles WHERE article_id = :articleId)")
    fun isArticleSaved(articleId: Int): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM viewed_articles WHERE article_id = :articleId)")
    fun isArticleViewed(articleId: Int): Boolean

} 