//package com.example.vnews.ui.components
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import coil.compose.AsyncImage
//import com.example.vnews.utils.DateTimeUtil.getRelativeTimeString
//import com.google.firebase.Timestamp
//import com.google.firebase.auth.FirebaseAuth
//
//@Composable
//fun MessageItem(
//    message: String,
//    senderName: String,
//    senderAvatar: String?,
//    timestamp: Timestamp?,
//    senderId: String,
//    previousSenderId: String? = null
//) {
//    val currentUser = FirebaseAuth.getInstance().currentUser
//    val isCurrentUser = currentUser?.uid == senderId
//    val showAvatarAndName = !isCurrentUser && previousSenderId != senderId
//
//    // Format relative time
//    val relativeTime = timestamp?.toDate()?.time?.let { getRelativeTimeString(it) } ?: ""
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 8.dp, vertical = 4.dp),
//        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start,
//        verticalAlignment = Alignment.Top
//    ) {
//        if (showAvatarAndName) {
//            // Avatar container
//            Box(
//                modifier = Modifier
//                    .size(40.dp)
//                    .clip(CircleShape)
//                    .background(MaterialTheme.colorScheme.surfaceVariant),
//                contentAlignment = Alignment.Center
//            ) {
//                if (senderAvatar != null) {
//                    AsyncImage(
//                        model = senderAvatar,
//                        contentDescription = "Sender avatar",
//                        modifier = Modifier.fillMaxSize(),
//                        contentScale = ContentScale.Crop
//                    )
//                } else {
//                    Icon(
//                        imageVector = Icons.Default.Person,
//                        contentDescription = "Default avatar",
//                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
//                        modifier = Modifier.size(24.dp)
//                    )
//                }
//            }
//            Spacer(modifier = Modifier.width(8.dp))
//        } else {
//            Spacer(modifier = Modifier.width(48.dp))
//        }
//
//        Column(
//            modifier = Modifier.weight(1f),
//            horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
//        ) {
//            if (showAvatarAndName) {
//                // Sender name
//                Text(
//                    text = senderName,
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//                Spacer(modifier = Modifier.height(2.dp))
//            }
//
//            // Message bubble
//            Column(
//                modifier = Modifier
//                    .background(
//                        if (isCurrentUser)
//                            MaterialTheme.colorScheme.primary
//                        else
//                            MaterialTheme.colorScheme.surfaceVariant
//                    )
//                    .padding(horizontal = 12.dp, vertical = 8.dp)
//            ) {
//                Text(
//                    text = message,
//                    style = MaterialTheme.typography.bodyLarge,
//                    color = if (isCurrentUser)
//                        MaterialTheme.colorScheme.onPrimary
//                    else
//                        MaterialTheme.colorScheme.onSurface
//                )
//                Text(
//                    text = relativeTime,
//                    style = MaterialTheme.typography.bodySmall,
//                    color = if (isCurrentUser)
//                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
//                    else
//                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
//                    textAlign = TextAlign.End,
//                    modifier = Modifier.padding(top = 4.dp)
//                )
//            }
//        }
//    }
//}