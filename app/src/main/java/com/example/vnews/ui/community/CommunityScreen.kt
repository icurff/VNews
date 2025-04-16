//package com.example.vnews.ui.screens
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import com.example.vnews.ui.components.BottomChatBar
//import com.example.vnews.ui.components.MessageItem
//import com.example.vnews.ui.viewmodel.ChatViewModel
//import com.example.vnews.ui.user.UserViewModel
//import kotlinx.coroutines.launch
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CommunityScreen(
//    navController: NavController,
//    chatViewModel: ChatViewModel = hiltViewModel(),
//    userViewModel: UserViewModel = hiltViewModel()
//) {
//    val messages by chatViewModel.messages.collectAsState()
//    val isLoggedIn by userViewModel.isLoggedIn.collectAsState()
//    val listState = rememberLazyListState()
//    val coroutineScope = rememberCoroutineScope()
//
//    // Improved auto-scroll when new messages arrive
//    LaunchedEffect(messages) {
//        if (messages.isNotEmpty()) {
//            coroutineScope.launch {
//                try {
//                    listState.animateScrollToItem(
//                        index = messages.size - 1,
//                        scrollOffset = 0
//                    )
//                } catch (e: Exception) {
//                    // Fallback to instant scroll if animation fails
//                    listState.scrollToItem(messages.size - 1)
//                }
//            }
//        }
//    }
//
//    // Add scroll to bottom when user sends a message
//    LaunchedEffect(Unit) {
//        coroutineScope.launch {
//            try {
//                listState.animateScrollToItem(
//                    index = messages.size - 1,
//                    scrollOffset = 0
//                )
//            } catch (e: Exception) {
//                listState.scrollToItem(messages.size - 1)
//            }
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Community") },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Back"
//                        )
//                    }
//                }
//            )
//        },
//        bottomBar = {
//            if (isLoggedIn) {
//                BottomChatBar()
//            } else {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = "Please login to join the conversation",
//                        style = MaterialTheme.typography.bodyLarge,
//                        textAlign = TextAlign.Center,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//            }
//        }
//    ) { paddingValues ->
//        if (messages.isEmpty()) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(paddingValues),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = if (isLoggedIn)
//                        "Be the first to start a conversation!"
//                    else
//                        "No messages yet. ",
//                    style = MaterialTheme.typography.bodyLarge,
//                    textAlign = TextAlign.Center,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            }
//        } else {
//            LazyColumn(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(paddingValues),
//                state = listState,
//                contentPadding = PaddingValues(vertical = 8.dp)
//            ) {
//                itemsIndexed(messages) { index, message ->
//                    val previousSenderId = if (index > 0) messages[index - 1].senderId else null
//                    MessageItem(
//                        message = message.message,
//                        senderName = message.senderName,
//                        senderAvatar = message.senderAvatar,
//                        timestamp = message.timestamp,
//                        senderId = message.senderId,
//                        previousSenderId = previousSenderId
//                    )
//                }
//            }
//        }
//    }
//}