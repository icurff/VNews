package com.example.vnews.ui.extension

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.composables.icons.lucide.CirclePlus
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Trash2
import com.example.vnews.R
import com.example.vnews.data.local.entity.ExtensionEntity
import com.example.vnews.data.remote.dto.RssSource
import com.example.vnews.ui.navigation.Screen
import com.example.vnews.ui.theme.NewsBlue
import com.example.vnews.utils.StringUtils
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtensionScreen(
    extensionViewModel: ExtensionViewModel,
    navController: NavController,
) {
    val selectedTab by extensionViewModel.selectedTab
    val error by extensionViewModel.error.collectAsState()
    val successMessage by extensionViewModel.successMessage.collectAsState()

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
                                    text = stringResource(R.string.installed),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTab == "Installed") NewsBlue else MaterialTheme.colorScheme.onSurface
                                )
                                if (selectedTab == "Installed") {
                                    Box(
                                        modifier = Modifier
                                            .height(3.dp)
                                            .width(35.dp)
                                            .background(
                                                NewsBlue,
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
                                    text = stringResource(R.string.library),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTab == "Library") NewsBlue else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (selectedTab == "Library") {
                                    Box(
                                        modifier = Modifier
                                            .height(3.dp)
                                            .width(35.dp)
                                            .background(
                                                NewsBlue,
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

            // Display error message if any
            error?.let { errorMessage ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { extensionViewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(errorMessage)
                }

                // Auto clear error after 3 seconds
                LaunchedEffect(errorMessage) {
                    delay(3000)
                    extensionViewModel.clearError()
                }
            }

            // Display success message if any
            successMessage?.let { message ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(message)
                }

                // Auto clear success message after 3 seconds
                LaunchedEffect(message) {
                    delay(3000)
                    extensionViewModel.clearSuccessMessage()
                }
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
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(exts) { extension ->
                    ExtensionItem(
                        extension = extension,
                        onClick = {},
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

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(installedExts) { extension ->
            InstalledItem(
                extension = extension,
                onDelete = { extensionViewModel.deleteExtension(extension) },
                onClick = {
                    extensionViewModel.setSelectedExtension(extension)
                    val encodedUrl = StringUtils.encodeUrl(extension.source)
                    navController.navigate(Screen.ExtensionDetail.createRoute(encodedUrl))
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
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = extension.icon,
                contentDescription = extension.name,
                modifier = Modifier
                    .size(52.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = extension.name,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = extension.source,
                    style = TextStyle(fontSize = 14.sp),
                )
            }

            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Lucide.CirclePlus,
                    contentDescription = "Add extension",
                    tint = NewsBlue,
                    modifier = Modifier.size(28.dp)
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
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = extension.icon,
                contentDescription = extension.name,
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = extension.name,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = extension.source,
                    style = TextStyle(fontSize = 12.sp),
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Lucide.Trash2,
                    contentDescription = "Delete extension",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}