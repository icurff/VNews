package com.example.vnews.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.vnews.data.local.entity.ExtensionEntity
import com.example.vnews.data.remote.dto.RssSource
import com.example.vnews.ui.navigation.Screen
import com.example.vnews.ui.viewmodel.ExtensionUiState
import com.example.vnews.ui.viewmodel.ExtensionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtensionsScreen(
    extensionViewModel: ExtensionViewModel,
    navController: NavController,
) {
    val selectedTab by extensionViewModel.selectedTab
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // installed
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { extensionViewModel.setSelectedTab("Installed") }
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Installed",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTab == "Installed") Color.Blue else Color.Gray
                                )
                                if (selectedTab == "Installed") {
                                    Box(
                                        modifier = Modifier
                                            .height(3.dp)
                                            .width(35.dp)
                                            .background(
                                                Color.Blue,
                                                shape = RoundedCornerShape(
                                                    topStart = 8.dp,
                                                    topEnd = 8.dp
                                                )
                                            )
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(3.dp))
                                }
                            }
                            // library
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { extensionViewModel.setSelectedTab("Library") }
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Library",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTab == "Library") Color.Blue else Color.Gray
                                )
                                if (selectedTab == "Library") {
                                    Box(
                                        modifier = Modifier
                                            .height(3.dp)
                                            .width(35.dp)
                                            .background(
                                                Color.Blue,
                                                shape = RoundedCornerShape(
                                                    topStart = 8.dp,
                                                    topEnd = 8.dp
                                                )
                                            )
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(3.dp))
                                }
                            }

                        }
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Repository.route) }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "menu"
                        )

                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (selectedTab) {
                "Installed" -> Installed(extensionViewModel, navController)
                "Library" -> Library(extensionViewModel)
                else -> {}
            }


        }
    }
}


@Composable
fun Library(extensionViewModel: ExtensionViewModel) {
    val uiState by extensionViewModel.uiState.collectAsState()

    when (uiState) {
        is ExtensionUiState.Loading -> {
            CircularProgressIndicator()
        }

        is ExtensionUiState.Success -> {
            val exts = (uiState as ExtensionUiState.Success).extensions
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(exts) { extension ->
                    ExtensionItem(
                        extension = extension,
                        onClick = {

                        },
                        onAddClick = { extensionViewModel.addExtension(extension) }
                    )
                }
            }
        }

        is ExtensionUiState.Error -> {
            Text(
                text = (uiState as ExtensionUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun Installed(extensionViewModel: ExtensionViewModel, navController: NavController) {
    val installedExts by extensionViewModel.installed.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(installedExts) { extension ->
            InstalledItem(
                extension = extension,
                onDelete = { extensionViewModel.deleteExtension(extension) },
                onClick = {
//                    extensionViewModel.setSelectedExtension(extension)
//                    navController.navigate(Screen.ExtensionDetail.createRoute(extension.link))
                }
            )
        }
    }
}

@Composable
fun ExtensionItem(
    extension: RssSource,
    onClick: () -> Unit,
    onAddClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = extension.icon,
                contentDescription = extension.name,
                modifier = Modifier
                    .size(64.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = extension.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2
                )
            }

            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add extension"
                )
            }
        }
    }
}

@Composable
fun InstalledItem(
    extension: ExtensionEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = extension.icon,
                contentDescription = extension.name,
                modifier = Modifier
                    .size(64.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = extension.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete extension",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}