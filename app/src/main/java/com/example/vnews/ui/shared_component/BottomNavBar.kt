package com.example.vnews.ui.shared_component

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.vnews.ui.navigation.Screen

val bottomNavItems = listOf(Screen.Home, Screen.Extension,Screen.Community, Screen.Settings)

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val parentRoute = navBackStackEntry?.destination?.parent?.route

    NavigationBar(
        modifier = Modifier.defaultMinSize(minHeight = 70.dp)
    ) {
        bottomNavItems.forEach { screen ->
            NavigationBarItem(
                modifier = Modifier.defaultMinSize(minHeight = 70.dp),
                icon = { screen.icon?.invoke() },
                label = { Text(screen.title) },
                selected = when (screen) {
                    Screen.Home -> parentRoute == screen.route || currentRoute == screen.route
                    else -> currentRoute == screen.route
                },
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}