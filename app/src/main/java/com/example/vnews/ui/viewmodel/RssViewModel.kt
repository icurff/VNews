    package com.example.vnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vnews.data.data_provider.Categories
import com.example.vnews.data.data_provider.ExtensionEntities
import com.example.vnews.utils.DateTimeUtil
import com.prof18.rssparser.RssParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject

data class RssItem(
//    val id: Int = 0,
    val title: String,
    val summary: String,
    val thumbnail: String,
    val source: String,
    val pubTime: Long,
    val extensionName: String,
    val extensionIcon: String
)

@HiltViewModel
class RssViewModel @Inject constructor(
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _rssItems = MutableStateFlow<Map<Int, List<RssItem>>>(emptyMap())
    val rssItems: StateFlow<Map<Int, List<RssItem>>> = _rssItems.asStateFlow()

    private val rssParser = RssParser()


    init {
        fetchAllCategories()
    }

    private fun fetchAllCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Categories.all.forEach { cate ->
                    fetchRssFeeds(categoryId = cate.id)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun handleRefreshFeed() {
        _isRefreshing.value = true
        try {
            fetchAllCategories()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            _isRefreshing.value = false
        }
    }

    private fun updateRssItems(categoryId: Int, newItems: List<RssItem>) {
        _rssItems.value = _rssItems.value.toMutableMap().apply {
            this[categoryId] = newItems
        }
    }

    private fun fetchRssFeeds(categoryId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val extensionEntities =
                    ExtensionEntities.getExtensionEntitiesByCategoryId(categoryId)
                val allItems = mutableListOf<RssItem>()

                withContext(Dispatchers.IO) {
                    extensionEntities.forEach { extension ->
                        try {
                            val channel = rssParser.getRssChannel(extension.source)

                            val items = channel.items.map { item ->

                                RssItem(
                                    title = Jsoup.parse(item.title ?: "").text()
                                        .replace("&apos;", "'"),
                                    summary = Jsoup.parse(item.description ?: "").text(),
                                    source = item.link ?: "",
                                    pubTime = DateTimeUtil.parseDateToUnix(item.pubDate ?: ""),
                                    thumbnail = item.image ?: "",
                                    extensionName = extension.name,
                                    extensionIcon = extension.icon
                                )
                            }
                            allItems.addAll(items)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                val sortedItems = allItems.sortedByDescending { it.pubTime }
                updateRssItems(categoryId, sortedItems)

            } catch (e: Exception) {
                e.printStackTrace()
                updateRssItems(categoryId, emptyList())
            } finally {
                _isLoading.value = false
            }
        }
    }


}