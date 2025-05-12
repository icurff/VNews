package com.example.vnews.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.composables.icons.lucide.LayoutGrid
import com.composables.icons.lucide.LayoutList
import com.composables.icons.lucide.LayoutPanelLeft
import com.composables.icons.lucide.Lucide
import com.example.vnews.data.data_provider.Categories
import com.example.vnews.ui.user_setting.AppSettingsManager
import com.example.vnews.ui.article.ArticleViewModel
import com.example.vnews.ui.home.component.RssFeedList
import com.example.vnews.ui.navigation.Screen
import com.example.vnews.ui.shared_component.BottomNavBar
import com.example.vnews.ui.theme.NewsGradient
import kotlinx.coroutines.launch

enum class LayoutType {
    LIST,
    GRID,
    EXPANDED
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    rssViewModel: RssViewModel,
    articleViewModel: ArticleViewModel,
    navController: NavController,
    appSettingsManager: AppSettingsManager
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
    var currentLayout by remember { mutableStateOf(LayoutType.LIST) }

    // Load saved layout type
    LaunchedEffect(Unit) {
        appSettingsManager.getAppSettings()?.let { settings ->
            currentLayout = settings.layoutType
        }
    }

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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = WindowInsets.statusBars
                            .asPaddingValues()
                            .calculateTopPadding()
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VNews",
                        style = TextStyle(
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            brush = Brush.horizontalGradient(NewsGradient)
                        ),
                        textAlign = TextAlign.Center,
                    )
                    IconButton(
                        onClick = { navController.navigate(Screen.Search.route) },
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                shape = CircleShape
                            ),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    edgePadding = 16.dp,
                    containerColor = Color.Transparent,
                    indicator = { },
                    divider = { }
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
                            text = {
                                Text(
                                    text = stringResource(id = tab.nameResId),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = if (selectedTabIndex == index) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            selected = selectedTabIndex == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            modifier = Modifier
                                .padding(horizontal = 4.dp, vertical = 8.dp)
                                .height(36.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (selectedTabIndex == index)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                        )
                    }
                }
            }
        },
        bottomBar = { BottomNavBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    currentLayout = when (currentLayout) {
                        LayoutType.LIST -> LayoutType.GRID
                        LayoutType.GRID -> LayoutType.EXPANDED
                        LayoutType.EXPANDED -> LayoutType.LIST
                    }
                    coroutineScope.launch {
                        appSettingsManager.setLayoutType(currentLayout)
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(50.dp).alpha(0.85f),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    imageVector = when (currentLayout) {
                        LayoutType.LIST -> Lucide.LayoutList
                        LayoutType.GRID -> Lucide.LayoutGrid
                        LayoutType.EXPANDED -> Lucide.LayoutPanelLeft
                    },
                    contentDescription = "Change Layout",

                    )
            }
        }
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
                searchQuery = null,
                layoutType = currentLayout
            )
        }
    }
}