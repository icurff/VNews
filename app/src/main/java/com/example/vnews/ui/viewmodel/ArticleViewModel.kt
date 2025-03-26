package com.example.vnews.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vnews.data.local.entity.ArticleEntity
import com.example.vnews.data.model.ArticleContent
import com.example.vnews.data.repository.ArticleRepository
import com.example.vnews.utils.WebScraper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val articleRepository: ArticleRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedArticle = MutableStateFlow<RssItem?>(null)
    val selectedArticle: StateFlow<RssItem?> = _selectedArticle.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    private val _viewedItems = MutableStateFlow<List<RssItem>>(emptyList())
    val viewedItems: StateFlow<List<RssItem>> = _viewedItems.asStateFlow()

    private val _savedItems = MutableStateFlow<List<RssItem>>(emptyList())
    val savedItems: StateFlow<List<RssItem>> = _savedItems.asStateFlow()

    private val _articleContent = MutableStateFlow<ArticleContent?>(null)
    val articleContent: StateFlow<ArticleContent?> = _articleContent.asStateFlow()


    init {
        getViewedArticles()
        getSavedArticles()
    }

    private fun getViewedArticles() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val articles = withContext(Dispatchers.IO) {
                    articleRepository.getViewedArticles().first()
                }
                _viewedItems.value = articles.toRssItemList()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getSavedArticles() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val articles = withContext(Dispatchers.IO) {
                    articleRepository.getSavedArticles().first()
                }
                _savedItems.value = articles.toRssItemList()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }


    private fun List<ArticleEntity>.toRssItemList(): List<RssItem> {
        return this.map { article ->
            RssItem(
                title = article.title ?: "No Title",
                summary = article.summary ?: "No Summary",
                source = article.source ?: "No Source",
                thumbnail = article.thumbnail ?: "",
                pubTime = article.postTime,
                extensionName = article.extensionName ?: "Unknown Extension",
                extensionIcon = article.extensionIcon ?: ""
            )
        }
    }

    fun setSelectedArticle(rssItem: RssItem) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _selectedArticle.value = rssItem

                _isSaved.value = withContext(Dispatchers.IO) {
                    articleRepository.isArticleSaved(rssItem).first()
                }
                launch(Dispatchers.IO) {
                    articleRepository.markArticleAsViewed(rssItem)
                }
                launch(Dispatchers.IO) {
                    _articleContent.value = WebScraper.fetchArticleContent(rssItem.source)
                }
            } catch (e: Exception) {
                Log.e("ArticleViewModel", "Error setting selected article", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markArticleAsSaved(rssItem: RssItem) {
        viewModelScope.launch {
            articleRepository.markArticleAsSaved(rssItem)
            _isSaved.value = true
        }
    }

    fun deleteSavedArticle(rssItem: RssItem) {
        viewModelScope.launch {
            articleRepository.deleteSavedArticle(rssItem)
            _isSaved.value = false
        }
    }

    fun clearSelectedArticle() {
        viewModelScope.launch {
            _selectedArticle.value = null
            _articleContent.value = null
            _isSaved.value = false
            _isLoading.value = false
        }
    }


}