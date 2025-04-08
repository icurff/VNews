package com.example.vnews.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.vnews.data.model.ArticleItem
import com.example.vnews.ui.components.BottomCommentBar
import com.example.vnews.ui.components.CommentItem
import com.example.vnews.ui.viewmodel.ArticleViewModel
import com.example.vnews.utils.DateTimeUtil
import com.example.vnews.utils.StringUtils
import com.example.vnews.utils.TextToSpeechUtil

// Text size presets
enum class TextSizePreset(val size: Float) {
    SMALL(14f),
    MEDIUM(16f),
    LARGE(18f)
}

// Font family presets
enum class FontPreset(val fontFamily: FontFamily) {
    DEFAULT(FontFamily.Default),
    ROBOTO(FontFamily.SansSerif),
    GEORGIA(FontFamily.Serif),
    COURIER(FontFamily.Monospace)
}

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

    // Text customization state
    var selectedTextSize by remember { mutableStateOf(TextSizePreset.MEDIUM) }
    var selectedFontFamily by remember { mutableStateOf(FontPreset.DEFAULT) }
    var showFontMenu by remember { mutableStateOf(false) }
    var showTextSizeMenu by remember { mutableStateOf(false) }
    var showCommentsSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    val encodedArticlePath = selectedArticle?.let { StringUtils.encodeUrl(it.source) }
    val comments by articleViewModel.comments.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            textToSpeechUtil.shutdown()
        }
    }
    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(28.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(
                        onClick = { showTextSizeMenu = !showTextSizeMenu },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.TextFormat,
                            contentDescription = "Text Size",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    IconButton(
                        onClick = { showFontMenu = !showFontMenu },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.TextFields,
                            contentDescription = "Font Family",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            if (isSpeaking) {
                                textToSpeechUtil.stop()
                            } else {
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
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                            contentDescription = if (isSpeaking) "Stop" else "Play",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    BadgedBox(
                        badge = {
                            if (comments.isNotEmpty()) {
                                Badge(
                                    containerColor = Color.Red,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = if (comments.size > 99) "99+" else comments.size.toString(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    ) {
                        IconButton(
                            onClick = { showCommentsSheet = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Comment,
                                contentDescription = "Comments",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            if (isArticleSaved) {
                                articleViewModel.deleteSavedArticle(selectedArticle!!)
                            } else {
                                articleViewModel.markArticleAsSaved(selectedArticle!!)
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isArticleSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = if (isArticleSaved) "Unsave" else "Save",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box {
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
                        text = DateTimeUtil.getRelativeTime(selectedArticle!!.pubTime),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item {
                    Text(
                        text = selectedArticle!!.summary,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = selectedTextSize.size.sp,
                            fontFamily = selectedFontFamily.fontFamily
                        ),
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
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = selectedTextSize.size.sp,
                                        fontFamily = selectedFontFamily.fontFamily
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            is ArticleItem.Image -> {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(item.url)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = item.caption,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    if (item.caption.isNotEmpty()) {
                                        Text(
                                            text = item.caption,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = selectedTextSize.size.sp,
                                                fontFamily = selectedFontFamily.fontFamily
                                            ),
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
                            val intent =
                                Intent(Intent.ACTION_VIEW, Uri.parse(selectedArticle!!.source))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Read Full Article")
                    }
                }
            }

            // Text Size Menu
            if (showTextSizeMenu) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Text("Text Size")
                        DropdownMenu(
                            expanded = showTextSizeMenu,
                            onDismissRequest = { showTextSizeMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Small") },
                                onClick = {
                                    selectedTextSize = TextSizePreset.SMALL
                                    showTextSizeMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Medium") },
                                onClick = {
                                    selectedTextSize = TextSizePreset.MEDIUM
                                    showTextSizeMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Large") },
                                onClick = {
                                    selectedTextSize = TextSizePreset.LARGE
                                    showTextSizeMenu = false
                                }
                            )
                        }
                    }
                }
            }

            // Font Family Menu
            if (showFontMenu) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Text("Font Family")
                        DropdownMenu(
                            expanded = showFontMenu,
                            onDismissRequest = { showFontMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Default") },
                                onClick = {
                                    selectedFontFamily = FontPreset.DEFAULT
                                    showFontMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Roboto") },
                                onClick = {
                                    selectedFontFamily = FontPreset.ROBOTO
                                    showFontMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Georgia") },
                                onClick = {
                                    selectedFontFamily = FontPreset.GEORGIA
                                    showFontMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Courier") },
                                onClick = {
                                    selectedFontFamily = FontPreset.COURIER
                                    showFontMenu = false
                                }
                            )
                        }
                    }
                }
            }

            // Comments Bottom Sheet
            if (showCommentsSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showCommentsSheet = false },
                    sheetState = sheetState,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(LocalConfiguration.current.screenHeightDp.dp * 0.8f)
                    ) {
                        // Comments List
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            // Comments Header
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                IconButton(
                                    onClick = { showCommentsSheet = false },
                                    modifier = Modifier.align(Alignment.CenterStart)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close"
                                    )
                                }

                                // Title in the center
                                Text(
                                    text = "Comments",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }


                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                if (comments.isEmpty()) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 32.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "No comments yet",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                } else {
                                    items(comments) { comment ->
                                        CommentItem(
                                            comment = comment.commentContent,
                                            senderName = comment.senderName,
                                            senderAvatar = comment.senderAvatar,
                                            timestamp = comment.timestamp
                                        )
                                    }
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                        ) {

                            if (encodedArticlePath != null) {
                                BottomCommentBar(encodedArticlePath)
                            }
                        }

                    }
                }
            }
        }
    }
}