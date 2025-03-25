package com.example.vnews.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.vnews.ui.components.BottomNavBar
import com.example.vnews.ui.components.RssFeedList
import com.example.vnews.ui.viewmodel.ArticleViewModel
import com.example.vnews.ui.viewmodel.RssViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun HomeScreen(
    rssViewModel: RssViewModel,
    articleViewModel: ArticleViewModel,
    navController: NavController
) {
    val tabs = Categories.all

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
        },
        bottomBar = { BottomNavBar(navController) }
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
                navController = navController
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