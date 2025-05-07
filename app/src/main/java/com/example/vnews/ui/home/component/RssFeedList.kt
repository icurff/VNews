package com.example.vnews.ui.home.component

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.vnews.ui.article.ArticleViewModel
import com.example.vnews.ui.home.LayoutType
import com.example.vnews.ui.home.RssItem
import com.example.vnews.ui.home.RssViewModel
import com.example.vnews.ui.navigation.Screen
import com.example.vnews.utils.DateTimeUtil
import com.example.vnews.utils.StringUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RssFeedList(
    rssViewModel: RssViewModel,
    articleViewModel: ArticleViewModel,
    categoryId: Int?,
    modifier: Modifier = Modifier,
    navController: NavController,
    searchQuery: String? = null,
    layoutType: LayoutType = LayoutType.LIST
) {
    val rssItems by rssViewModel.rssItems.collectAsState()
    val isLoading by rssViewModel.isLoading.collectAsState()
//    val isRefreshing by rssViewModel.isRefreshing.collectAsState()

    // If categoryId is null, get items from all categories for global search
    val itemsList = if (categoryId != null) {
        rssItems[categoryId] ?: emptyList()
    } else {
        // Combine items from all categories
        rssItems.values.flatten()
    }

    val filteredItems = if (!searchQuery.isNullOrBlank()) {
        itemsList.filter { item ->
            item.title.contains(searchQuery, ignoreCase = true) ||
                    item.summary.contains(searchQuery, ignoreCase = true)
        }
    } else {
        itemsList
    }

    if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = {
                rssViewModel.handleRefreshFeed()
            },
            modifier = modifier
        ) {
            when (layoutType) {
                LayoutType.LIST -> {
                    LazyColumn(
                        modifier = modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredItems) { item ->
                            RssItemCard(
                                item = item,
                                onClick = {
                                    handleRssItemCardClick(
                                        item,
                                        articleViewModel,
                                        navController
                                    )
                                },
                                isGridLayout = false,
                                isExpandedLayout = false
                            )
                        }
                    }
                }

                LayoutType.GRID -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredItems) { item ->
                            RssItemCard(
                                item = item,
                                onClick = {
                                    handleRssItemCardClick(
                                        item,
                                        articleViewModel,
                                        navController
                                    )
                                },
                                isGridLayout = true,
                                isExpandedLayout = false
                            )
                        }
                    }
                }

                LayoutType.EXPANDED -> {
                    LazyColumn(
                        modifier = modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredItems) { item ->
                            RssItemCard(
                                item = item,
                                onClick = {
                                    handleRssItemCardClick(
                                        item,
                                        articleViewModel,
                                        navController
                                    )
                                },
                                isGridLayout = false,
                                isExpandedLayout = true
                            )
                        }
                    }
                }
            }
        }
    }
}

fun handleRssItemCardClick(
    item: RssItem,
    articleViewModel: ArticleViewModel,
    navController: NavController
) {
    articleViewModel.setSelectedArticle(item)
    val encodedUrl = StringUtils.encodeUrl(item.source)
    navController.navigate(Screen.ArticleDetail.createRoute(encodedUrl))
}

@Composable
private fun RssItemCard(
    item: RssItem,
    onClick: () -> Unit,
    isGridLayout: Boolean,
    isExpandedLayout: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                )
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        if (isExpandedLayout) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                AsyncImage(
                    model = item.thumbnail,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = item.title,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.summary,
                    style = TextStyle(fontSize = 14.sp),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = item.extensionIcon,
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 4.dp)
                        )
                        Text(
                            text = item.extensionName,
                            style = TextStyle(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = DateTimeUtil.getRelativeTimeString(item.pubTime),
                        style = TextStyle(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else if (isGridLayout) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                AsyncImage(
                    model = item.thumbnail,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.title,
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = item.extensionIcon,
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(end = 4.dp)
                        )
                        Text(
                            text = item.extensionName,
                            style = TextStyle(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = DateTimeUtil.getRelativeTimeString(item.pubTime),
                        style = TextStyle(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                AsyncImage(
                    model = item.thumbnail,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                ) {
                    Text(
                        text = item.title,
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = item.extensionIcon,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(end = 4.dp)
                            )
                            Text(
                                text = item.extensionName,
                                style = TextStyle(fontSize = 12.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = DateTimeUtil.getRelativeTimeString(item.pubTime),
                            style = TextStyle(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
} 