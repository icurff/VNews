//package com.example.vnews.ui.components
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.Send
//import androidx.compose.material.icons.automirrored.outlined.Send
//import androidx.compose.material.icons.filled.Image
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.material3.TextFieldDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Color.Companion.Gray
//import androidx.compose.ui.graphics.RectangleShape
//import androidx.compose.ui.unit.dp
//import com.google.firebase.Timestamp
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FieldValue
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.launch
//
//@Composable
//fun BottomChatBar() {
//    var messageText by remember { mutableStateOf("") }
//
//    val db = FirebaseFirestore.getInstance()
//    val auth = FirebaseAuth.getInstance()
//    val currentUser = auth.currentUser
//    val scope = rememberCoroutineScope()
//    Column {
//        Spacer(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(0.5.dp)
//                .background(Gray)
//        )
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            TextField(
//                value = messageText,
//                onValueChange = { messageText = it },
//                placeholder = { Text("Input message") },
//                modifier = Modifier.weight(1f),
//                shape = RectangleShape,
//                colors = TextFieldDefaults.colors(
//                    unfocusedIndicatorColor = Color.Transparent,
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedContainerColor = Color.Transparent,
//                    focusedContainerColor = Color.Transparent
//                )
//            )
//
////            IconButton(onClick ={}) {
////                Icon(
////                    imageVector = Icons.Filled.Image,
////                    contentDescription = "Attach image",
////                    tint = Gray
////                )
////            }
//            IconButton(onClick = {
//                if (messageText.isNotBlank() && currentUser != null) {
//                    scope.launch {
//                        val messageData = hashMapOf(
//                            "message" to messageText,
//                            "senderId" to currentUser.uid,
//                            "senderName" to (currentUser.displayName ?: "Guest"),
//                            "senderAvatar" to currentUser.photoUrl?.toString(),
//                            "timestamp" to FieldValue.serverTimestamp()
//                        )
//                        db.collection("messages").add(messageData)
//                        messageText = ""
//                    }
//                }
//            }) {
//                Icon(
//                    Icons.AutoMirrored.Outlined.Send,
//                    contentDescription = "Send",
//                    tint = Gray
//                )
//            }
//        }
//    }
//}