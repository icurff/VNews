package com.example.vnews.utils

import com.example.vnews.data.model.ArticleContent
import com.example.vnews.data.model.ArticleItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

object WebScraper {
    suspend fun fetchArticleContent(url: String): ArticleContent {
        return withContext(Dispatchers.IO) {
            val doc = Jsoup.connect(url).get()
            val items = mutableListOf<ArticleItem>()

            // Chọn nội dung chính
            val content = doc.selectFirst("article.fck_detail")
                ?: doc.selectFirst("div.detail-content")
                ?: doc.selectFirst("div.article__body")
                ?: doc.selectFirst("div.singular-content")
                ?: doc.selectFirst("div.maincontent.main-content")

            content?.children()?.forEach { element ->
                when (element.tagName()) {
                    "p" -> {
                        val text = element.text().trim()
                        if (text.isNotEmpty()) {
                            items.add(ArticleItem.Text(text))
                        }
                    }

                    "div" -> {
                        if (element.hasClass("VCSortableInPreviewMode")) {
                            // Xử lý danh sách ảnh trong album
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
                        // Xử lý ảnh đơn lẻ
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
        }
    }
}

