package com.example.vnews.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vnews.ui.components.BottomChatBar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(navController: NavController) {
    var messages by remember { mutableStateOf(listOf<Message>()) }
    val db = remember { FirebaseFirestore.getInstance() }
    val auth = remember { FirebaseAuth.getInstance() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Initial scroll to latest message when screen is first loaded
    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Fetch messages from Firebase
    LaunchedEffect(Unit) {
        db.collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                snapshot?.let { documents ->
                    val messageList = documents.mapNotNull { doc ->
                        doc.toObject(Message::class.java)
                    }
                    messages = messageList
                    // Scroll to the latest message
                    coroutineScope.launch {
                        if (messages.isNotEmpty()) {
                            listState.animateScrollToItem(messages.size - 1)
                        }
                    }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        },
        bottomBar = {
            BottomChatBar()
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = listState
        ) {
            items(messages) { message ->
                MessageItem(
                    message = message,
                    isCurrentUser = message.senderId == auth.currentUser?.uid
                )
            }
        }
    }
}

@Composable
fun MessageItem(message: Message, isCurrentUser: Boolean) {
    val alignment = if (isCurrentUser) Alignment.End else Alignment.Start
    val backgroundColor = if (isCurrentUser) MaterialTheme.colorScheme.primary else Color.Gray

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = alignment
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 1.dp,
            color = backgroundColor
        ) {
            Text(
                text = "${message.message}\n${message.timestamp?.toDate()?.toString() ?: ""}",
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

data class Message(
    var message: String = "",
    var senderId: String? = null,
    var timestamp: Timestamp? = null
)