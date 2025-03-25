package com.example.vnews.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.vnews.data.model.ArticleItem
import com.example.vnews.ui.viewmodel.ArticleViewModel
import com.example.vnews.ui.viewmodel.RssViewModel
import com.example.vnews.util.DateTimeUtils
import com.example.vnews.util.TextToSpeechUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    onBackClick: () -> Unit,
    articleViewModel: ArticleViewModel
) {
    val context = LocalContext.current
    val selectedArticle by articleViewModel.selectedArticle.collectAsState()
    val articleContent by articleViewModel.articleContent.collectAsState()
    val isLoading by articleViewModel.isLoading.collectAsState()
    val isArticleSaved by articleViewModel.isSaved.collectAsState()
    
    val textToSpeechUtil = remember { TextToSpeechUtil(context) }
    val isSpeaking by textToSpeechUtil.isSpeaking.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            textToSpeechUtil.shutdown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedArticle!!.extensionName) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (isSpeaking) {
                                textToSpeechUtil.stop()
                            } else {
                                // Combine title, summary, and content for TTS
                                val fullText = buildString {
                                    append(selectedArticle!!.title)
                                    append(". ")
                                    append(selectedArticle!!.summary)
                                    append(". ")
                                    articleContent?.items?.forEach { item ->
                                        if (item is ArticleItem.Text) {
                                            append(item.content)
                                            append(". ")
                                        }
                                    }
                                }
                                textToSpeechUtil.speak(fullText)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                            contentDescription = if (isSpeaking) "Stop" else "Play"
                        )
                    }
                    IconButton(
                        onClick = {
                            if (isArticleSaved) {
                                articleViewModel.deleteSavedArticle(selectedArticle!!)
                            } else {
                                articleViewModel.markArticleAsSaved(selectedArticle!!)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isArticleSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = if (isArticleSaved) "Unsave" else "Save"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = selectedArticle!!.extensionIcon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = selectedArticle!!.extensionName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            item {
                Text(
                    text = selectedArticle!!.title,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Start
                )
            }

            item {
                Text(
                    text = DateTimeUtils.getRelativeTime(selectedArticle!!.pubTime),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Text(
                    text = selectedArticle!!.summary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                items(articleContent?.items ?: emptyList()) { item ->
                    when (item) {
                        is ArticleItem.Text -> {
                            Text(
                                text = item.content,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        is ArticleItem.Image -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                AsyncImage(
                                    model = item.url,
                                    contentDescription = item.caption,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                if (item.caption.isNotEmpty()) {
                                    Text(
                                        text = item.caption,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selectedArticle!!.source))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Read Full Article")
                }
            }
        }
    }
}