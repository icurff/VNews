package com.example.vnews.ui.components

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.vnews.ui.navigation.Screen
import com.example.vnews.ui.viewmodel.ArticleViewModel
import com.example.vnews.ui.viewmodel.RssItem
import com.example.vnews.ui.viewmodel.RssViewModel
import com.example.vnews.utils.DateTimeUtil
import com.example.vnews.utils.StringUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RssFeedList(
    rssViewModel: RssViewModel,
    articleViewModel: ArticleViewModel,
    categoryId: Int,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val rssItems by rssViewModel.rssItems.collectAsState()
    val isLoading by rssViewModel.isLoading.collectAsState()
//    val isRefreshing by rssViewModel.isRefreshing.collectAsState()
    val itemsList = rssItems[categoryId] ?: emptyList()


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
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(itemsList) { item ->
                    RssItemCard(
                        item = item,
                        onClick = { handleRssItemCardClick(item, articleViewModel, navController) }
                    )
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
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            AsyncImage(
                model = item.thumbnail,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.summary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = item.extensionIcon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = item.extensionName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                }
                Text(
                    text = DateTimeUtil.getRelativeTime(item.pubTime),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }


        }
    }
} 