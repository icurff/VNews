//package com.example.vnews.ui.components
//
//import ScreenTabs
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.Menu
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material3.CenterAlignedTopAppBar
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.runtime.Composable
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.example.vnews.ui.viewmodel.ExtensionViewModel
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CustomTopAppBar(
//    tabs: List<ScreenTabs>,
//    selectedTab: ScreenTabs,
//    barViewModel: ExtensionViewModel = hiltViewModel(),
//    onMenuClick: ()->Unit
//) {
//    Column {
//        // Top App Bar
//        AnimatedVisibility(
//            visible = !barViewModel.isSearchMode.value,
////            enter = fadeIn(),
////            exit = fadeOut()
//        ) {
//            CenterAlignedTopAppBar(
//                navigationIcon = {
//                    IconButton(onClick = {}) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Back"
//                        )
//                    }
//                },
//                title = {
//                    TabsRow(
//                        tabs = tabs,
//                        selectedTab = selectedTab,
//                        onTabSelected = barViewModel::onTabSelected
//                    )
//                },
//                actions = {
//                    Row {
//                        IconButton(onClick = { barViewModel.onSearchClick() }) {
//                            Icon(
//                                imageVector = Icons.Filled.Search,
//                                contentDescription = "search"
//                            )
//                        }
//                        IconButton(onClick = { onMenuClick()}) {
//                            Icon(
//                                imageVector = Icons.Filled.Menu,
//                                contentDescription = "menu"
//                            )
//                        }
//                    }
//                }
//            )
//
//        }
//
//    }
//}
