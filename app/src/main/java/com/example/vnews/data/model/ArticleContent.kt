package com.example.vnews.data.model

data class ArticleContent(
    val items: List<ArticleItem>
)

sealed class ArticleItem {
    data class Text(val content: String) : ArticleItem()
    data class Image(
        val url: String,
        val caption: String
    ) : ArticleItem()
}