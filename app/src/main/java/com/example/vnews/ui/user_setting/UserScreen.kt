package com.example.vnews.ui.user_setting

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.vnews.R
import com.example.vnews.ui.shared_component.BottomNavBar
import com.example.vnews.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    navController: NavController,
    viewModel: UserViewModel = hiltViewModel(),
    appSettingsViewModel: AppSettingsViewModel = hiltViewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val userPhotoUrl by viewModel.userPhotoUrl.collectAsState()
    val appSettings by appSettingsViewModel.appSettings.collectAsState()
    val context = LocalContext.current

    var showLanguageDropdown by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.user_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    if (isLoggedIn) {
                        IconButton(onClick = { viewModel.signOut(context as Activity) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = stringResource(R.string.logout)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                UserProfile(
                    isLoggedIn = isLoggedIn,
                    userName = userName,
                    userPhotoUrl = userPhotoUrl,
                    onGoogleSignInClick = { viewModel.signInWithGoogle(context as Activity) }
                )
            }

            // App Section
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Gray)
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.app_section),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Theme Setting
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DarkMode,
                                contentDescription = stringResource(R.string.dark_mode),
                                tint = Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = stringResource(R.string.dark_mode),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        Switch(
                            checked = appSettings.isDarkTheme,
                            onCheckedChange = { appSettingsViewModel.setDarkTheme(it) }
                        )
                    }
                }
            }

            // Language Setting
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Language,
                                contentDescription = stringResource(R.string.language),
                                tint = Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = stringResource(R.string.language),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        
                        Box {
                            Text(
                                text = when (appSettings.language) {
                                    "en" -> stringResource(R.string.english)
                                    "vi" -> stringResource(R.string.vietnamese)
                                    else -> stringResource(R.string.english)
                                },
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.clickable { showLanguageDropdown = true }
                            )
                            
                            DropdownMenu(
                                expanded = showLanguageDropdown,
                                onDismissRequest = { showLanguageDropdown = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.english)) },
                                    onClick = {
                                        appSettingsViewModel.setLanguage("en")
                                        showLanguageDropdown = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.vietnamese)) },
                                    onClick = {
                                        appSettingsViewModel.setLanguage("vi")
                                        showLanguageDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // App - Saved Articles
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(Screen.SavedArticles.route) }
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.BookmarkBorder,
                            contentDescription = stringResource(R.string.saved_articles),
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = stringResource(R.string.saved_articles),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }

            // App - Viewed Articles
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(Screen.ViewedArticles.route) }
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Visibility,
                            contentDescription = stringResource(R.string.viewed_articles),
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = stringResource(R.string.viewed_articles),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }

            // Connection Section
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Gray)
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.connection_section),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Connection - Fb
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Public,
                            contentDescription = stringResource(R.string.follow_facebook),
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = stringResource(R.string.follow_facebook),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserProfile(
    isLoggedIn: Boolean,
    userName: String,
    userPhotoUrl: String?,
    onGoogleSignInClick: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(0.5.dp, MaterialTheme.colorScheme.onSurface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isLoggedIn && userPhotoUrl != null) {
                    AsyncImage(
                        model = userPhotoUrl,
                        contentDescription = "User Profile Picture",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_user),
                        contentDescription = "User Icon",
                        modifier = Modifier.size(70.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = userName, fontWeight = FontWeight.Bold)
        }
        if (!isLoggedIn) {
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = onGoogleSignInClick,
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = stringResource(R.string.login_with_google),
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.login_with_google),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
