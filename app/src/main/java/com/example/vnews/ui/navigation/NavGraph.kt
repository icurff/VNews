package com.example.vnews.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.vnews.ui.screens.ArticleDetailScreen
import com.example.vnews.ui.screens.CommunityScreen
import com.example.vnews.ui.screens.ExtensionsScreen
import com.example.vnews.ui.screens.HomeScreen
import com.example.vnews.ui.screens.RepositoryScreen
import com.example.vnews.ui.screens.SavedArticlesScreen
import com.example.vnews.ui.screens.UserScreen
import com.example.vnews.ui.screens.ViewedArticlesScreen
import com.example.vnews.ui.screens.ExtensionDetailScreen
import com.example.vnews.ui.viewmodel.ArticleViewModel
import com.example.vnews.ui.viewmodel.ExtensionViewModel
import com.example.vnews.ui.viewmodel.RepositoryViewModel
import com.example.vnews.ui.viewmodel.RssViewModel

sealed class Screen(
    val route: String,
    val title: String = "",
    val icon: @Composable (() -> Unit)? = null
) {
    object Home : Screen(
        route = "home_graph",
        title = "Home",
        icon = { Icon(Icons.Default.Home, contentDescription = "Home") })

    object Extension : Screen(
        route = "extension",
        title = "Extensions",
        icon = { Icon(Icons.Default.Build, contentDescription = "extensions") }
    )

    object Community : Screen(
        route = "community",
        title = "Community",
        icon = { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Settings") }
    )

    object Settings : Screen(
        route = "settings",
        title = "User",
        icon = { Icon(Icons.Default.Settings, contentDescription = "User") }
    )

    object ArticleDetail : Screen("article/{articleSource}") {
        fun createRoute(articleSource: String) = "article/$articleSource"
    }

    object ExtensionDetail : Screen("extension/{extensionSource}") {
        fun createRoute(extensionSource: String) = "extension/$extensionSource"
    }

    object SavedArticles : Screen("saved_articles")

    object ViewedArticles : Screen("viewed_articles")

    object Repository : Screen("repository")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        composable(Screen.Community.route) {
            CommunityScreen(navController)
        }

        navigation(
            route = "home_graph",
            startDestination = "home",

            ) {
            composable("home") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("home_graph")
                }
                val rssViewModel = hiltViewModel<RssViewModel>(parentEntry)
                val articleViewModel = hiltViewModel<ArticleViewModel>(parentEntry)

                HomeScreen(
                    rssViewModel = rssViewModel,
                    articleViewModel = articleViewModel,
                    navController = navController
                )
            }

            composable(
                route = Screen.ArticleDetail.route,
                enterTransition = {
                    fadeIn(
                        animationSpec = tween(
                            durationMillis = 300, easing = FastOutSlowInEasing
                        )
                    ) + slideIntoContainer(
                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                        towards = AnimatedContentTransitionScope.SlideDirection.Start
                    )
                },
                exitTransition = {
                    fadeOut(
                        animationSpec = tween(
                            durationMillis = 300, easing = FastOutSlowInEasing
                        )
                    ) + slideOutOfContainer(
                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                        towards = AnimatedContentTransitionScope.SlideDirection.Start
                    )
                },
                popEnterTransition = {
                    fadeIn(
                        animationSpec = tween(
                            300, easing = FastOutSlowInEasing
                        )
                    ) + slideIntoContainer(
                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                        towards = AnimatedContentTransitionScope.SlideDirection.End
                    )
                },
                popExitTransition = {
                    fadeOut(
                        animationSpec = tween(
                            300, easing = FastOutSlowInEasing
                        )
                    ) + slideOutOfContainer(
                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                        towards = AnimatedContentTransitionScope.SlideDirection.End
                    )
                }
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("home_graph")
                }
                val articleViewModel = hiltViewModel<ArticleViewModel>(parentEntry)

                ArticleDetailScreen(
                    articleViewModel = articleViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
        navigation(
            route = "extension_graph",
            startDestination = Screen.Extension.route,
        ) {
            composable(route = Screen.Extension.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("extension_graph")
                }
                val extensionViewModel = hiltViewModel<ExtensionViewModel>(parentEntry)
                ExtensionsScreen(
                    extensionViewModel = extensionViewModel,
                    navController = navController
                )
            }
            composable(route = Screen.Repository.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("extension_graph")
                }
                val exRepositoryViewModel = hiltViewModel<RepositoryViewModel>(parentEntry)
                RepositoryScreen(exRepositoryViewModel)
            }
            composable(route = Screen.ExtensionDetail.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("extension_graph")
                }
                val extensionViewModel = hiltViewModel<ExtensionViewModel>(parentEntry)
                val articleViewModel = hiltViewModel<ArticleViewModel>(parentEntry)
                ExtensionDetailScreen(
                    extensionViewModel = extensionViewModel,
                    articleViewModel = articleViewModel,
                    navController = navController,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.ArticleDetail.route,
                enterTransition = {
                    fadeIn(
                        animationSpec = tween(
                            durationMillis = 300, easing = FastOutSlowInEasing
                        )
                    ) + slideIntoContainer(
                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                        towards = AnimatedContentTransitionScope.SlideDirection.Start
                    )
                },
                exitTransition = {
                    fadeOut(
                        animationSpec = tween(
                            durationMillis = 300, easing = FastOutSlowInEasing
                        )
                    ) + slideOutOfContainer(
                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                        towards = AnimatedContentTransitionScope.SlideDirection.Start
                    )
                },
                popEnterTransition = {
                    fadeIn(
                        animationSpec = tween(
                            300, easing = FastOutSlowInEasing
                        )
                    ) + slideIntoContainer(
                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                        towards = AnimatedContentTransitionScope.SlideDirection.End
                    )
                },
                popExitTransition = {
                    fadeOut(
                        animationSpec = tween(
                            300, easing = FastOutSlowInEasing
                        )
                    ) + slideOutOfContainer(
                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                        towards = AnimatedContentTransitionScope.SlideDirection.End
                    )
                }
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("extension_graph")
                }
                val articleViewModel = hiltViewModel<ArticleViewModel>(parentEntry)

                ArticleDetailScreen(
                    articleViewModel = articleViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
        navigation(route = "settings_graph", startDestination = "settings") {
            composable(Screen.Settings.route) { UserScreen(navController) }

            composable(
                route = Screen.SavedArticles.route,
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("settings_graph")
                }
                val articleViewModel = hiltViewModel<ArticleViewModel>(parentEntry)

                SavedArticlesScreen(
                    title = "Saved Articles",
                    navController = navController,
                    articleViewModel = articleViewModel
                )
            }

            composable(
                route = Screen.ViewedArticles.route,
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("settings_graph")
                }
                val articleViewModel = hiltViewModel<ArticleViewModel>(parentEntry)

                ViewedArticlesScreen(
                    title = "Viewed Articles",
                    navController = navController,
                    articleViewModel = articleViewModel
                )
            }

            composable(
                route = Screen.ArticleDetail.route,
                enterTransition = {
                    fadeIn(
                        animationSpec = tween(
                            durationMillis = 300, easing = FastOutSlowInEasing
                        )
                    ) + slideIntoContainer(
                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                        towards = AnimatedContentTransitionScope.SlideDirection.Start
                    )
                },
                exitTransition = {
                    fadeOut(
                        animationSpec = tween(
                            durationMillis = 300, easing = FastOutSlowInEasing
                        )
                    ) + slideOutOfContainer(
                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                        towards = AnimatedContentTransitionScope.SlideDirection.Start
                    )
                },
                popEnterTransition = {
                    fadeIn(
                        animationSpec = tween(
                            300, easing = FastOutSlowInEasing
                        )
                    ) + slideIntoContainer(
                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                        towards = AnimatedContentTransitionScope.SlideDirection.End
                    )
                },
                popExitTransition = {
                    fadeOut(
                        animationSpec = tween(
                            300, easing = FastOutSlowInEasing
                        )
                    ) + slideOutOfContainer(
                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                        towards = AnimatedContentTransitionScope.SlideDirection.End
                    )
                }
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("settings_graph")
                }
                val articleViewModel = hiltViewModel<ArticleViewModel>(parentEntry)

                ArticleDetailScreen(
                    articleViewModel = articleViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

