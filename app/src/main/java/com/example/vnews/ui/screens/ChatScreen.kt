package com.example.vnews.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

data class ChatMessage(
    val id: Int,
    val text: String,
    val sender: String,
    val time: String,
    val color: Color,
    val borderColor: Color? = null,
    val verified: Boolean = false,
    val avatarUrl: String? = null,
    val isMe: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                id = 1,
                text = "đổi",
                sender = "me",
                time = "27 minutes",
                color = Color(0xFF3A3A3C),
                borderColor = Color(0xFFE74C3C),
                isMe = true
            ),
            ChatMessage(
                id = 2,
                sender = "Quân Nguyễn",
                text = "sửa như nào ạ,chỉ tôi với @@",
                time = "26 minutes",
                color = Color(0xFF1E3A5F),
                verified = true,
                avatarUrl = "https://picsum.photos/200"
            ),
            ChatMessage(
                id = 3,
                sender = "MAKED ◊",
                text = "gg cách xử lý khi bị mạng chặn",
                time = "26 minutes",
                color = Color(0xFF3A3A3C),
                borderColor = Color(0xFFE74C3C),
                verified = true,
                avatarUrl = "https://picsum.photos/201"
            ),
            ChatMessage(
                id = 4,
                sender = "ngatngay",
                text = "cách sửa mạng",
                time = "25 minutes",
                color = Color(0xFF1E3A5F),
                verified = true,
                avatarUrl = "https://picsum.photos/202"
            ),
            ChatMessage(
                id = 5,
                sender = "ngatngay",
                text = "😏",
                time = "25 minutes",
                color = Color(0xFF1E3A5F),
                verified = true,
                avatarUrl = "https://picsum.photos/202"
            ),
            ChatMessage(
                id = 6,
                sender = "ngatngay",
                text = "gọi kỹ thuật viettel cho nhanh, t gọi suốt bạn ạ",
                time = "24 minutes",
                color = Color(0xFF1E3A5F),
                verified = true,
                avatarUrl = "https://picsum.photos/202"
            ),
            ChatMessage(
                id = 7,
                sender = "MAKED ◊",
                text = "gọi báo bọn nó mở port cũng đc mà hầu như là chỉ đc thời gian ngắn",
                time = "23 minutes",
                color = Color(0xFF3A3A3C),
                borderColor = Color(0xFFE74C3C),
                verified = true,
                avatarUrl = "https://picsum.photos/201"
            ),
            ChatMessage(
                id = 8,
                sender = "ngatngay",
                text = "😢",
                time = "12 minutes",
                color = Color(0xFF1E3A5F),
                verified = true,
                avatarUrl = "https://picsum.photos/202"
            )
        )
    }

    var messageText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back navigation */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle menu */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Black
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Input message") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp)
                    )

                    IconButton(onClick = { /* Handle emoji */ }) {
                        Icon(
                            Icons.Outlined.EmojiEmotions,
                            contentDescription = "Emoji",
                            tint = Color.Gray
                        )
                    }
                    IconButton(onClick = { /* Handle image attachment */ }) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = "Attach image",
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            reverseLayout = false,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(messages) { message ->
                MessageItem(message = message)
            }
        }
    }
}

@Composable
fun MessageItem(message: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isMe) Alignment.End else Alignment.Start
    ) {
        // Show sender info only for messages not from the current user
        if (!message.isMe) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Box(modifier = Modifier.size(32.dp)) {
                    AsyncImage(
                        model = message.avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color(0xFF333333), CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    if (message.verified) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = 2.dp, y = 2.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1DA1F2))
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Verified",
                                tint = Color.White,
                                modifier = Modifier.size(8.dp)
                            )
                        }
                    }
                }

                val senderColor = when (message.sender) {
                    "Quân Nguyễn" -> Color(0xFF1DA1F2)
                    "MAKED ◊" -> Color(0xFFE74C3C)
                    "ngatngay" -> Color(0xFF2ECC71)
                    else -> Color.White
                }

                Text(
                    text = message.sender,
                    color = senderColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Row(verticalAlignment = Alignment.Bottom) {
            if (!message.isMe) {
                Spacer(modifier = Modifier.width(32.dp))
            }

            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(message.color)
                    .then(
                        if (message.borderColor != null) {
                            Modifier.border(
                                1.dp,
                                message.borderColor,
                                RoundedCornerShape(12.dp)
                            )
                        } else {
                            Modifier
                        }
                    )
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = message.text,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Text(
                        text = message.time,
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun chatPreview(){
    ChatScreen()
}