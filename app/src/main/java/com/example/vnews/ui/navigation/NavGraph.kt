package com.example.vnews.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Layers
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MessageCircleMore
import com.composables.icons.lucide.User
import com.example.vnews.R
import com.example.vnews.ui.user_setting.AppSettingsManager
import com.example.vnews.ui.article.ArticleDetailScreen
import com.example.vnews.ui.article.ArticleViewModel
import com.example.vnews.ui.article.SavedArticlesScreen
import com.example.vnews.ui.article.ViewedArticlesScreen
import com.example.vnews.ui.community.CommunityScreen
import com.example.vnews.ui.extension.ExtensionDetailScreen
import com.example.vnews.ui.extension.ExtensionScreen
import com.example.vnews.ui.extension.ExtensionViewModel
import com.example.vnews.ui.extension_source.ExtensionSourceScreen
import com.example.vnews.ui.extension_source.ExtensionSourceViewModel
import com.example.vnews.ui.home.HomeScreen
import com.example.vnews.ui.home.RssViewModel
import com.example.vnews.ui.search.SearchScreen
import com.example.vnews.ui.user_setting.UserScreen

object NavTitles {
    val HOME = R.string.nav_home
    val EXTENSIONS = R.string.nav_extensions
    val COMMUNITY = R.string.nav_community
    val SETTINGS = R.string.nav_user
}

sealed class Screen(
    val route: String,
    val titleResId: Int = 0,
    val title: String = "",
    val icon: @Composable (() -> Unit)? = null
) {
    object Home : Screen(
        route = "home_graph",
        titleResId = NavTitles.HOME,
        icon = { Icon(Lucide.House, contentDescription = "Home") })

    object Extension : Screen(
        route = "extension",
        titleResId = NavTitles.EXTENSIONS,
        icon = { Icon(Lucide.Layers, contentDescription = "extensions") }
    )

    object Community : Screen(
        route = "community",
        titleResId = NavTitles.COMMUNITY,
        icon = { Icon(Lucide.MessageCircleMore, contentDescription = "Settings") }
    )

    object Settings : Screen(
        route = "settings",
        titleResId = NavTitles.SETTINGS,
        icon = { Icon(Lucide.User, contentDescription = "User") }
    )

    object ArticleDetail : Screen("article/{articleSource}") {
        fun createRoute(articleSource: String) = "article/$articleSource"
    }

    object ExtensionDetail : Screen("extension/{extensionSource}") {
        fun createRoute(extensionSource: String) = "extension/$extensionSource"
    }

    object SavedArticles : Screen("saved_articles")

    object ViewedArticles : Screen("viewed_articles")

    object Search : Screen("search")

    object Repository : Screen("repository")
    
    // Helper function to get localized title in a Composable context
    @Composable
    fun getLocalizedTitle(): String {
        return if (titleResId != 0) {
            stringResource(id = titleResId)
        } else {
            title
        }
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    appSettingsManager: AppSettingsManager
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        composable(route = Screen.Community.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                ) + fadeIn(animationSpec = tween(700))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                ) + fadeOut(animationSpec = tween(700))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                ) + fadeIn(animationSpec = tween(700))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                ) + fadeOut(animationSpec = tween(700))
            }) {
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
                    navController = navController,
                    appSettingsManager = appSettingsManager
                )
            }

            composable(
                route = Screen.ArticleDetail.route,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    ) + fadeIn(animationSpec = tween(700))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    ) + fadeOut(animationSpec = tween(700))
                },
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    ) + fadeIn(animationSpec = tween(700))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    ) + fadeOut(animationSpec = tween(700))
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

            composable(
                route = Screen.Search.route,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    ) + fadeIn(animationSpec = tween(700))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    ) + fadeOut(animationSpec = tween(700))
                },
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    ) + fadeIn(animationSpec = tween(700))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    ) + fadeOut(animationSpec = tween(700))
                }
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("home_graph")
                }
                val rssViewModel = hiltViewModel<RssViewModel>(parentEntry)
                val articleViewModel = hiltViewModel<ArticleViewModel>(parentEntry)

                SearchScreen(
                    rssViewModel = rssViewModel,
                    articleViewModel = articleViewModel,
                    navController = navController
                )
            }
        }
        navigation(
            route = "extension_graph",
            startDestination = Screen.Extension.route,
        ) {
            composable(
                route = Screen.Extension.route,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    ) + fadeIn(animationSpec = tween(700))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    ) + fadeOut(animationSpec = tween(700))
                },
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    ) + fadeIn(animationSpec = tween(700))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    ) + fadeOut(animationSpec = tween(700))
                }
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("extension_graph")
                }
                val extensionViewModel = hiltViewModel<ExtensionViewModel>(parentEntry)
                ExtensionScreen(
                    extensionViewModel = extensionViewModel,
                    navController = navController
                )
            }
            composable(
                route = Screen.Repository.route,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    ) + fadeIn(animationSpec = tween(700))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    ) + fadeOut(animationSpec = tween(700))
                },
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    ) + fadeIn(animationSpec = tween(700))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    ) + fadeOut(animationSpec = tween(700))
                }
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("extension_graph")
                }
                val extensionSourceViewModel = hiltViewModel<ExtensionSourceViewModel>(parentEntry)
                ExtensionSourceScreen(extensionSourceViewModel)
            }
            composable(
                route = Screen.ExtensionDetail.route,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    ) + fadeIn(animationSpec = tween(700))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    ) + fadeOut(animationSpec = tween(700))
                },
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    ) + fadeIn(animationSpec = tween(700))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    ) + fadeOut(animationSpec = tween(700))
                }
            ) { backStackEntry ->
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
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    ) + fadeIn(animationSpec = tween(700))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    ) + fadeOut(animationSpec = tween(700))
                },
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    ) + fadeIn(animationSpec = tween(700))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    ) + fadeOut(animationSpec = tween(700))
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
            composable(
                route = Screen.Settings.route,
            ) { UserScreen(navController) }

            composable(
                route = Screen.SavedArticles.route,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    ) + fadeIn(animationSpec = tween(700))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    ) + fadeOut(animationSpec = tween(700))
                },
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    ) + fadeIn(animationSpec = tween(700))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    ) + fadeOut(animationSpec = tween(700))
                }
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("settings_graph")
                }
                val articleViewModel = hiltViewModel<ArticleViewModel>(parentEntry)

                SavedArticlesScreen(
                    title = stringResource(R.string.saved_articles),
                    navController = navController,
                    articleViewModel = articleViewModel
                )
            }

            composable(
                route = Screen.ViewedArticles.route,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    ) + fadeIn(animationSpec = tween(700))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    ) + fadeOut(animationSpec = tween(700))
                },
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    ) + fadeIn(animationSpec = tween(700))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    ) + fadeOut(animationSpec = tween(700))
                }
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("settings_graph")
                }
                val articleViewModel = hiltViewModel<ArticleViewModel>(parentEntry)

                ViewedArticlesScreen(
                    title = stringResource(R.string.viewed_articles),
                    navController = navController,
                    articleViewModel = articleViewModel
                )
            }

            composable(
                route = Screen.ArticleDetail.route,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    ) + fadeIn(animationSpec = tween(700))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    ) + fadeOut(animationSpec = tween(700))
                },
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    ) + fadeIn(animationSpec = tween(700))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    ) + fadeOut(animationSpec = tween(700))
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

