package com.example.vnews.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vnews.data.data_provider.Categories
import com.example.vnews.ui.article.ArticleViewModel
import com.example.vnews.ui.home.component.RssFeedList
import com.example.vnews.ui.shared_component.BottomNavBar
import kotlinx.coroutines.launch
import kotlin.math.abs

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    rssViewModel: RssViewModel,
    articleViewModel: ArticleViewModel,
    navController: NavController
) {
    val tabs = Categories.all
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { tabs.size }
    )
    val selectedTabIndex by remember { derivedStateOf { pagerState.currentPage } }
    var previousTabIndex by remember { mutableIntStateOf(0) }
    var targetTabIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(pagerState.currentPageOffsetFraction) {
        val scrollFraction = pagerState.currentPageOffsetFraction
        if (scrollFraction > 0) {
            previousTabIndex = pagerState.currentPage
            targetTabIndex = previousTabIndex + 1
        }
        if (scrollFraction < 0) {
            previousTabIndex = pagerState.currentPage
            targetTabIndex = previousTabIndex - 1
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchActive) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Search news...") },
                            singleLine = true
                        )
                    } else {
                        ScrollableTabRow(
                            selectedTabIndex = selectedTabIndex,
                            edgePadding = 0.dp,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.smoothTabIndicatorOffset(
                                        previousTabPosition = tabPositions[previousTabIndex],
                                        newTabPosition = tabPositions[targetTabIndex],
                                        swipeProgress = pagerState.currentPageOffsetFraction
                                    )
                                )
                            }
                        ) {
                            tabs.forEachIndexed { index, tab ->
                                Tab(
                                    text = { Text(tab.name) },
                                    selected = selectedTabIndex == index,
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    },
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { isSearchActive = !isSearchActive }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = { BottomNavBar(navController) },
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = {
//                    // Get the current category's RSS items
//                    val currentCategoryId = tabs[selectedTabIndex].id
//                    val currentRssItems = rssViewModel.rssItems.value[currentCategoryId] ?: emptyList()
//
//                    // Add the first few items to the playlist
//                    currentRssItems.take(5).forEach { article ->
//
//                    }
//
//                    // Navigate to the news player screen
//                    navController.navigate(Screen.NewsPlayer.route)
//                }
//            ) {
//                Icon(
//                    imageVector = Icons.Default.PlayArrow,
//                    contentDescription = "Play News"
//                )
//            }
//        }
    ) { innerPadding ->
        HorizontalPager(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            state = pagerState
        ) { pageIndex ->
            RssFeedList(
                rssViewModel = rssViewModel,
                articleViewModel = articleViewModel,
                categoryId = tabs[pageIndex].id,
                modifier = Modifier.fillMaxSize(),
                navController = navController,
                searchQuery = if (isSearchActive) searchQuery else null
            )
        }
    }
}

fun Modifier.smoothTabIndicatorOffset(
    previousTabPosition: TabPosition,
    newTabPosition: TabPosition,
    swipeProgress: Float
): Modifier = composed {
    val currentTabWidth =
        previousTabPosition.width + (newTabPosition.width - previousTabPosition.width) * abs(
            swipeProgress
        )
    val indicatorOffset =
        previousTabPosition.left + (newTabPosition.left - previousTabPosition.left) * abs(
            swipeProgress
        )
    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset { IntOffset(x = indicatorOffset.roundToPx(), y = 0) }
        .width(currentTabWidth)
}