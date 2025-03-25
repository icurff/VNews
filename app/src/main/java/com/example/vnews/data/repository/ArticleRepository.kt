package com.example.vnews.data.repository

import com.example.vnews.data.local.dao.ArticleDao
import com.example.vnews.data.local.dao.SavedArticleDao
import com.example.vnews.data.local.dao.ViewedArticleDao
import com.example.vnews.data.local.entity.ArticleEntity
import com.example.vnews.data.local.entity.SavedArticleEntity
import com.example.vnews.data.local.entity.ViewedArticleEntity
import com.example.vnews.ui.viewmodel.RssItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ArticleRepository @Inject constructor(
    private val articleDao: ArticleDao,
    private val savedArticleDao: SavedArticleDao,
    private val viewedArticleDao: ViewedArticleDao
) {
    fun getSavedArticles(): Flow<List<ArticleEntity>> {
        return articleDao.getSavedArticles()

    }

    fun getViewedArticles(): Flow<List<ArticleEntity>> {
        return articleDao.getViewedArticles()

    }

    suspend fun markArticleAsViewed(rssItem: RssItem) {
        val articleId = articleDao.getArticleIdBySource(rssItem.source) ?: articleDao.insertArticle(rssItem.toArticleEntity()).toInt()
        if (!articleDao.isArticleViewed(articleId)) {
            viewedArticleDao.markArticleAsViewed(ViewedArticleEntity(articleId = articleId))
        }
    }

    suspend fun markArticleAsSaved(rssItem: RssItem) {
        val articleId = articleDao.getArticleIdBySource(rssItem.source) ?: articleDao.insertArticle(
            rssItem.toArticleEntity()
        ).toInt()
        savedArticleDao.markArticleAsSaved(SavedArticleEntity(articleId = articleId))
    }

    suspend fun deleteSavedArticle(rssItem: RssItem) {
        val articleId = articleDao.getArticleIdBySource(rssItem.source)
        if (articleId != null) {
            savedArticleDao.deleteSavedArticleById(articleId)
        }
    }

    suspend fun isArticleSaved(rssItem: RssItem): Flow<Boolean> {
        val articleId = articleDao.getArticleIdBySource(rssItem.source)
        return if (articleId != null) {
            savedArticleDao.isArticleSaved(articleId)
        } else {
            flow { emit(false) }
        }
    }

    private fun RssItem.toArticleEntity(): ArticleEntity {
        return ArticleEntity(
            title = title,
            summary = summary,
            source = source,
            thumbnail = thumbnail,
            postTime = pubTime,
            extensionName = extensionName,
            extensionIcon = extensionIcon
        )
    }

}


