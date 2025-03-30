package com.example.vnews.ui.screens

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.vnews.R
import com.example.vnews.ui.components.BottomNavBar
import com.example.vnews.ui.components.PasswordTextField
import com.example.vnews.ui.navigation.Screen
import com.example.vnews.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    navController: NavController,
    viewModel: UserViewModel = hiltViewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val userPhotoUrl by viewModel.userPhotoUrl.collectAsState()
    val context = LocalContext.current



    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("User") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (isLoggedIn) {
                        IconButton(onClick = { viewModel.signOut(context as Activity) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Log out"
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
                        text = "App",
                        fontWeight = FontWeight.Bold
                    )
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
                            contentDescription = "Saved Icon",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )

                        Text(
                            text = "Saved Articles",
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
                            contentDescription = "Viewed Icon",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )

                        Text(
                            text = "Viewed Articles",
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
                        text = "Connection",
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
                            contentDescription = "Saved Icon",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )

                        Text(
                            text = "Follow Facebook page",
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
//    var showRegisterDialog by remember { mutableStateOf(false) }
//    var showLoginDialog by remember { mutableStateOf(false) }
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
                    contentDescription = "Google Icon",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Login with Google",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
//        Spacer(modifier = Modifier.width(16.dp))
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            Button(onClick = { showLoginDialog = true }, modifier = Modifier.weight(1f)) {
//                Text("Login")
//            }
//            Button(onClick = { showRegisterDialog = true }, modifier = Modifier.weight(1f)) {
//                Text("Register")
//            }
//        }
//        RegisterDialog(
//            showDialog = showRegisterDialog,
//            onDismiss = { showRegisterDialog = false })
//        LoginDialog(
//            showDialog = showLoginDialog,
//            onDismiss = { showLoginDialog = false },
//            onLoginGoogle = onGoogleSignInClick
//        )
    }
}

//@Composable
//fun RegisterDialog(showDialog: Boolean, onDismiss: () -> Unit) {
//    if (showDialog) {
//        AlertDialog(
//            onDismissRequest = { onDismiss() },
//            title = { Text("Register") },
//            text = {
//                Column {
//                    var userName by remember { mutableStateOf("") }
//                    var email by remember { mutableStateOf("") }
//                    var password by remember { mutableStateOf("") }
//
//                    OutlinedTextField(
//                        value = userName,
//                        onValueChange = { userName = it },
//                        label = { Text("User Name") }
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
//                    OutlinedTextField(
//                        value = email,
//                        onValueChange = { email = it },
//                        label = { Text("Email") }
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
//                    PasswordTextField(password, onPasswordChange = { password = it })
//                }
//            },
//            confirmButton = {
//                Button(onClick = { onDismiss() }) {
//                    Text("Submit")
//                }
//            },
//        )
//    }
//}
//
//@Composable
//fun LoginDialog(showDialog: Boolean, onDismiss: () -> Unit, onLoginGoogle: () -> Unit) {
//    if (showDialog) {
//        AlertDialog(
//            onDismissRequest = { onDismiss() },
//            title = { Text("Login") },
//            text = {
//                Column {
//                    var email by remember { mutableStateOf("") }
//                    var password by remember { mutableStateOf("") }
//
//                    OutlinedTextField(
//                        value = email,
//                        onValueChange = { email = it },
//                        label = { Text("Email") }
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
//                    PasswordTextField(password, onPasswordChange = { password = it })
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.End
//                    ) {
//                        Text(text = "Forgot Password ?", modifier = Modifier.clickable { })
//                    }
//                }
//            },
//            confirmButton = {
//                Column(modifier = Modifier.fillMaxWidth()) {
//                    Button(modifier = Modifier.fillMaxWidth(), onClick = {
//
//                        onDismiss()
//                    }) {
//                        Text("Login")
//                    }
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Button(modifier = Modifier.fillMaxWidth(), onClick = {
//                        onLoginGoogle()
//                        onDismiss()
//                    }) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_google),
//                            contentDescription = "Google Icon",
//                            modifier = Modifier.size(24.dp),
//                            tint = Color.Unspecified
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text(
//                            text = "Login with Google",
//                            color = Color.Black,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//                }
//            },
//        )
//    }
//}
//
