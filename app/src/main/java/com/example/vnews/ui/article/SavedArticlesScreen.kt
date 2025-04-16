package com.example.vnews.ui.article

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.vnews.ui.shared_component.ArticleList


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedArticlesScreen(
    title: String,
    navController: NavController,
    articleViewModel: ArticleViewModel
) {
    val rssItems by articleViewModel.savedItems.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        ArticleList(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            rssItems = rssItems,
            navController = navController,
            articleViewModel = articleViewModel,
        )
    }
}