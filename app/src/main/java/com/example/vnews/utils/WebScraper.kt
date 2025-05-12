package com.example.vnews.utils

import android.util.Log
import com.example.vnews.data.model.ArticleContent
import com.example.vnews.data.model.ArticleItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object WebScraper {
    suspend fun fetchArticleContent(url: String): ArticleContent {
        return withContext(Dispatchers.IO) {
            try {
                val doc = Jsoup.connect(url).get()
                val items = mutableListOf<ArticleItem>()

                val content = doc.selectFirst("article.fck_detail")
                    ?: doc.selectFirst("div.detail-content")
                    ?: doc.selectFirst("div.article__body")
                    ?: doc.selectFirst("div.singular-content")
                    ?: doc.selectFirst("div.maincontent.main-content")

                content?.children()?.forEach { element ->
                    when (element.tagName()) {
                        "p", "h1", "h2", "h3"-> {
                            val text = element.text().trim()
                            if (text.isNotEmpty()) {
                                items.add(ArticleItem.Text(text))
                            }
                        }

                        "div" -> {
                            if (element.hasClass("VCSortableInPreviewMode")) {
                                // multi figure
                                val images = element.select("figure.media-item img")
                                val caption = element.selectFirst("figcaption p")?.text()?.trim() ?: ""

                                images.forEach { img ->
                                    val sourceUrl = img.attr("data-original")
                                        .takeIf { it.isNotEmpty() }
                                        ?: img.attr("src")

                                    if (sourceUrl.isNotEmpty()) {
                                        items.add(ArticleItem.Image(sourceUrl, caption))
                                    }
                                }
                            }
                        }

                        "figure" -> {
                            // single figure
                            val img = element.selectFirst("img")
                            val sourceUrl = img?.attr("data-src")
                                ?.takeIf { it.isNotEmpty() }
                                ?: img?.attr("data-original")
                                    ?.takeIf { it.isNotEmpty() }
                                ?: img?.attr("src")

                            val caption = element.selectFirst("figcaption")?.text()?.trim() ?: ""

                            if (!sourceUrl.isNullOrEmpty()) {
                                items.add(ArticleItem.Image(sourceUrl, caption))
                            }
                        }
                    }
                }
                ArticleContent(items)
            } catch (e: UnknownHostException) {
                // No internet connection
                Log.e("WebScraper", "No internet connection", e)
                ArticleContent(listOf(ArticleItem.Text("Unable to load article: No internet connection")))
            } catch (e: SocketTimeoutException) {
                // Connection timeout
                Log.e("WebScraper", "Connection timeout", e)
                ArticleContent(listOf(ArticleItem.Text("Unable to load article: Connection timeout")))
            } catch (e: IOException) {
                // Network error
                Log.e("WebScraper", "Network error", e)
                ArticleContent(listOf(ArticleItem.Text("Unable to load article: Network error")))
            } catch (e: Exception) {
                // General error
                Log.e("WebScraper", "Error fetching article content", e)
                ArticleContent(listOf(ArticleItem.Text("Unable to load article: ${e.message ?: "Unknown error"}")))
            }
        }
    }
}

