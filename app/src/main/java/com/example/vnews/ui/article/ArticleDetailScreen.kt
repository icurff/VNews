package com.example.vnews.ui.article

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.vnews.R
import com.example.vnews.data.model.ArticleItem
import com.example.vnews.ui.article.component.AudioController
import com.example.vnews.ui.article.component.BottomCommentBar
import com.example.vnews.ui.article.component.CommentItem
import com.example.vnews.ui.article.component.SpeedControlSheet
import com.example.vnews.utils.DateTimeUtil
import com.example.vnews.utils.StringUtils
import com.example.vnews.utils.TextToSpeechUtil
import kotlinx.coroutines.launch

// Text size presets
enum class TextSizePreset(val size: Float) {
    EXTRA_SMALL(10f),
    SMALL(12f),
    MEDIUM(14f),
    LARGE(16f),
    EXTRA_LARGE(18f),
    HUGE(20f)
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
    articleViewModel: ArticleViewModel,
    onBackClick: () -> Unit,
) {
    val isSummarizing by articleViewModel.isSummarizing.collectAsState()
    val selectedArticle by articleViewModel.selectedArticle.collectAsState()
    val articleContent by articleViewModel.articleContent.collectAsState()
    val isLoading by articleViewModel.isLoading.collectAsState()
    val isArticleSaved by articleViewModel.isSaved.collectAsState()

    // Text customization states
    var selectedTextSize by remember { mutableStateOf(TextSizePreset.MEDIUM) }
    var selectedFontFamily by remember { mutableStateOf(FontPreset.DEFAULT) }

    val fontCustomizationSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showFontCustomizationSheet by remember { mutableStateOf(false) }

    val commentSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showCommentsSheet by remember { mutableStateOf(false) }

    val speedSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSpeedControlSheet by remember { mutableStateOf(false) }

    var showMenuBar by remember { mutableStateOf(false) }
    var showAudioController by remember { mutableStateOf(true) }


    val coroutineScope = rememberCoroutineScope()
    val encodedArticlePath = selectedArticle?.let { StringUtils.encodeUrl(it.source) }
    val comments by articleViewModel.comments.collectAsState()
    val articleSummary by articleViewModel.articleSummary.collectAsState()

    val context = LocalContext.current
    val textToSpeechUtil = remember { TextToSpeechUtil(context, articleViewModel) }
    val isSpeaking by articleViewModel.ttsIsSpeaking.collectAsState()
    val isPaused by articleViewModel.ttsIsPaused.collectAsState()

    val currentTtsItemIndex by articleViewModel.ttsCurrentItemIndex.collectAsState()

    val speechRate by articleViewModel.ttsSpeechRate.collectAsState()

    val ttsContentItems = remember(articleContent) {
        val items = mutableListOf<String>()
        if (selectedArticle != null) {
            items.add(selectedArticle!!.title)
            items.add(selectedArticle!!.summary)
        }

        if (articleContent != null) {
            articleContent!!.items.forEach { item ->
                if (item is ArticleItem.Text) {
                    items.add(item.content)
                }
            }
        }
        items
    }

    LaunchedEffect(ttsContentItems) {
        if (ttsContentItems.isNotEmpty()) {
            textToSpeechUtil.setContentItems(ttsContentItems)
        }
    }


    DisposableEffect(Unit) {
        onDispose {
            textToSpeechUtil.shutdown()
        }
    }

    val handleBackClick: () -> Unit = {
        textToSpeechUtil.shutdown()
        onBackClick()
    }

    BackHandler {
        textToSpeechUtil.shutdown()
        onBackClick()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AsyncImage(
                            model = selectedArticle?.extensionIcon,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = selectedArticle?.extensionName ?: "Unknown Source",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = handleBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showFontCustomizationSheet = true }) {
                        Icon(
                            imageVector = Icons.Filled.TextFormat,
                            contentDescription = "Text Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            if (selectedArticle != null && articleContent != null) {
                AnimatedVisibility(
                    visible = showMenuBar && !isSpeaking && !isPaused,
                    enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(300)
                    ),
                    exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(300)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .padding(
                                bottom = WindowInsets.navigationBars
                                    .asPaddingValues()
                                    .calculateBottomPadding()
                            )
                            .clip(RoundedCornerShape(16.dp)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = handleBackClick,
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
                            // TTS service
                            IconButton(
                                onClick = {
                                    showMenuBar = false
                                    showAudioController = true
                                    if (!isSpeaking) {
                                        coroutineScope.launch {
                                            textToSpeechUtil.playContentItem(0)
                                        }
                                    }
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Play",
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            // Comment Sheet
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
                            // Save Article
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
                            // Share Icon
                            IconButton(
                                onClick = {
                                    val shareIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "${selectedArticle!!.title}\n\nRead more at: ${selectedArticle!!.source}"
                                        )
                                        type = "text/plain"
                                    }
                                    context.startActivity(
                                        Intent.createChooser(
                                            shareIntent,
                                            "Share Article"
                                        )
                                    )
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Share,
                                    contentDescription = "Share",
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }

                // Audio Controller
                AnimatedVisibility(
                    visible = (isSpeaking || isPaused) && showAudioController,
                    enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(300)
                    ),
                    exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(300)
                    )
                ) {
                    AudioController(
                        isSpeaking = isSpeaking,
                        isPaused = isPaused,
                        currentItemIndex = currentTtsItemIndex,
                        totalItems = ttsContentItems.size,
                        currentItemTitle = if (currentTtsItemIndex >= 0 && currentTtsItemIndex < ttsContentItems.size) {
                            val text = ttsContentItems[currentTtsItemIndex]
                            if (text.length > 30) text.substring(0, 30) + "..." else text
                        } else "Not playing",
                        speechRate = speechRate,
                        onPlayPause = {
                            if (isSpeaking && !isPaused) {
                                textToSpeechUtil.pause()
                            } else if (isPaused) {
                                textToSpeechUtil.resume()
                            } else if (currentTtsItemIndex >= 0) {
                                textToSpeechUtil.playContentItem(currentTtsItemIndex)
                            } else {
                                textToSpeechUtil.playContentItem(0)
                            }
                        },
                        onNext = {
                            textToSpeechUtil.playNextItem()
                        },
                        onPrevious = {
                            textToSpeechUtil.playPreviousItem()
                        },
                        onClose = {
                            textToSpeechUtil.stop()
                        },
                        onShowSpeedControl = {
                            if (isSpeaking && !isPaused) {
                                textToSpeechUtil.pause()
                            }
                            showSpeedControlSheet = true
                        }
                    )
                }

                // Speed Control Bottom Sheet
                if (showSpeedControlSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showSpeedControlSheet = false },
                        sheetState = speedSheetState,
                        dragHandle = null
                    ) {
                        SpeedControlSheet(
                            currentSpeed = speechRate,
                            minSpeed = TextToSpeechUtil.MIN_SPEECH_RATE,
                            maxSpeed = TextToSpeechUtil.MAX_SPEECH_RATE,
                            step = TextToSpeechUtil.SPEECH_RATE_STEP,
                            onSpeedChange = { newRate ->
                                textToSpeechUtil.setSpeechRate(newRate)
                            },
                            onDismiss = {
                                showSpeedControlSheet = false
                            },
                            onResume = {
                                textToSpeechUtil.resume()
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectTapGestures {
                        if (isSpeaking || isPaused) {
                            showAudioController = !showAudioController
                        } else {
                            showMenuBar = !showMenuBar
                        }
                    }
                }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Article Title
                item {
                    Text(
                        text = selectedArticle?.title ?: "",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 8.dp)
                            .background(
                                if (currentTtsItemIndex == 0) MaterialTheme.colorScheme.primaryContainer.copy(
                                    alpha = 0.5f
                                )
                                else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(if (currentTtsItemIndex == 0) 8.dp else 0.dp)
                    )
                }
                // Article Pub time
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = DateTimeUtil.getRelativeTime(selectedArticle?.pubTime ?: 0),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        IconButton(
                            onClick = {
                                if (articleSummary?.isNotEmpty() == true) {
                                    articleViewModel.clearSummary()
                                } else {
                                    articleViewModel.summarizeArticle()
                                }
                            }
                        ) {
                            if (articleSummary?.isNotEmpty() == true) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Đóng tóm tắt"
                                )
                            } else {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.ai_chip_icon),
                                    contentDescription = "Tóm tắt"
                                )
                            }
                        }
                    }
                }
                // summarize with AI
                if (isSummarizing || articleSummary?.isNotEmpty() == true) {
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            val borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .drawBehind {
                                        drawRoundRect(
                                            color = borderColor,
                                            style = Stroke(
                                                width = 2f,
                                                pathEffect = PathEffect.dashPathEffect(
                                                    floatArrayOf(
                                                        10f,
                                                        10f
                                                    ), 0f
                                                )
                                            ),
                                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                                                12.dp.toPx()
                                            )
                                        )
                                    }
                                    .padding(16.dp)
                            ) {
//                                Column {
//                                    Text(
//                                        text = "Summarized by AI",
//                                        style = MaterialTheme.typography.titleMedium,
//                                        color = MaterialTheme.colorScheme.primary
//                                    )
//
//                                    if (isSummarizing) {
//                                        Box(
//                                            modifier = Modifier
//                                                .fillMaxWidth()
//                                                .padding(vertical = 16.dp),
//                                            contentAlignment = Alignment.Center
//                                        ) {
//                                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
//                                        }
//                                    } else if (articleSummary?.isNotEmpty() == true) {
//                                        Text(
//                                            text = articleSummary!!,
//                                            style = MaterialTheme.typography.bodyMedium,
//                                            modifier = Modifier.padding(top = 8.dp)
//                                        )
//                                    }
//                                }
                                Column {
                                    if (isSummarizing) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Summarizing...",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                        }
                                    } else if (articleSummary?.isNotEmpty() == true) {
                                        Text(
                                            text = "Summarized by AI",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = articleSummary!!,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(top = 8.dp)
                                        )
                                    }
                                }

                            }
                        }
                    }
                }
                // Article Lead
                item {
                    Text(
                        text = selectedArticle?.summary ?: "",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = selectedTextSize.size.sp,
                            fontFamily = selectedFontFamily.fontFamily
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .background(
                                if (currentTtsItemIndex == 1) MaterialTheme.colorScheme.primaryContainer.copy(
                                    alpha = 0.5f
                                )
                                else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(if (currentTtsItemIndex == 1) 8.dp else 0.dp)
                    )
                }

                // Article Contents
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else {

                    val textContentToIndexMap =
                        mutableMapOf<String, Int>() // map store article contents and its indices
                    var indexCounter = 2 // Start from 2 as title is 0 and summary is 1

                    articleContent?.items?.forEach { item ->
                        if (item is ArticleItem.Text) {
                            textContentToIndexMap[item.content] = indexCounter++
                        }
                    }

                    items(articleContent?.items ?: emptyList()) { item ->
                        when (item) {
                            is ArticleItem.Text -> {
                                val itemIndex = textContentToIndexMap[item.content] ?: -1
                                Text(
                                    text = item.content,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = selectedTextSize.size.sp,
                                        fontFamily = selectedFontFamily.fontFamily
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .background(
                                            if (currentTtsItemIndex == itemIndex) MaterialTheme.colorScheme.primaryContainer.copy(
                                                alpha = 0.5f
                                            )
                                            else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(if (currentTtsItemIndex == itemIndex) 8.dp else 0.dp)
                                )
                            }

                            is ArticleItem.Image -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
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
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                // Read Full Button
                item {
                    Button(
                        onClick = {
                            val source = selectedArticle?.source ?: return@Button
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(source))
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Read Full Here")
                    }
                }
            }

            // Font Customization Bottom Sheet
            if (showFontCustomizationSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showFontCustomizationSheet = false },
                    sheetState = fontCustomizationSheetState,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    ) {
                        Text(
                            text = "Tùy chỉnh",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center
                        )

                        // Font Type selection
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Kiểu chữ",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { selectedFontFamily = FontPreset.DEFAULT },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedFontFamily == FontPreset.DEFAULT)
                                            MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (selectedFontFamily == FontPreset.DEFAULT)
                                            MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                ) {
                                    Text("SF San-serif")
                                }

                                Button(
                                    onClick = { selectedFontFamily = FontPreset.GEORGIA },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedFontFamily == FontPreset.GEORGIA)
                                            MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (selectedFontFamily == FontPreset.GEORGIA)
                                            MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                ) {
                                    Text("Bookerly")
                                }

                                Button(
                                    onClick = { selectedFontFamily = FontPreset.ROBOTO },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedFontFamily == FontPreset.ROBOTO)
                                            MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (selectedFontFamily == FontPreset.ROBOTO)
                                            MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                ) {
                                    Text("Roboto")
                                }

                                Button(
                                    onClick = { selectedFontFamily = FontPreset.COURIER },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedFontFamily == FontPreset.COURIER)
                                            MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (selectedFontFamily == FontPreset.COURIER)
                                            MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                ) {
                                    Text("Courier")
                                }
                            }
                        }

                        // Font Size slider
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                        ) {
                            Text(
                                text = "Cỡ chữ",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "A",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(end = 8.dp)
                                )

                                Slider(
                                    value = TextSizePreset.entries.indexOf(selectedTextSize)
                                        .toFloat(),
                                    onValueChange = { value ->
                                        val index = value.toInt()
                                            .coerceIn(0, TextSizePreset.entries.size - 1)
                                        selectedTextSize = TextSizePreset.entries[index]
                                    },
                                    valueRange = 0f..5f,
                                    steps = 4,
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = "A",
                                    style = MaterialTheme.typography.headlineLarge,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Comments Bottom Sheet
            if (showCommentsSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showCommentsSheet = false },
                    sheetState = commentSheetState,
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