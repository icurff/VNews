package com.example.vnews.ui.community.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.vnews.utils.DateTimeUtil.getRelativeTimeString
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MessageItem(
    message: String,
    senderName: String,
    senderAvatar: String?,
    timestamp: Timestamp?,
    senderId: String,
    previousSenderId: String? = null,
    imageUrl: String? = null
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val isCurrentUser = currentUser?.uid == senderId
    val showAvatarAndName = !isCurrentUser && previousSenderId != senderId
    var showFullImage by remember { mutableStateOf(false) }

    // Format relative time
    val relativeTime = timestamp?.toDate()?.time?.let { getRelativeTimeString(it) } ?: ""
    val context = LocalContext.current

    // Dialog to show full image
    if (showFullImage && !imageUrl.isNullOrBlank()) {
        Dialog(
            onDismissRequest = { showFullImage = false },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showFullImage = false },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Full-size image",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
                
                // Close button in the top right corner
                IconButton(
                    onClick = { showFullImage = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(48.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close image",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 1.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (showAvatarAndName) {
            // Avatar container
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (senderAvatar != null) {
                    AsyncImage(
                        model = senderAvatar,
                        contentDescription = "Sender avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default avatar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }

        Column(
            modifier = Modifier.weight(1f, fill = false),
            horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
        ) {
            if (showAvatarAndName) {
                // Sender name
                Text(
                    text = senderName,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
            }

            // Message bubble
            Column(
                modifier = Modifier
                    .background(
                        color = if (isCurrentUser)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .padding(
                        horizontal = if (imageUrl.isNullOrBlank()) 12.dp else 4.dp,
                        vertical = if (imageUrl.isNullOrBlank()) 8.dp else 4.dp
                    )
            ) {
                // Show image if present
                if (!imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Attached image",
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showFullImage = true },
                        contentScale = ContentScale.FillWidth
                    )
                    
                    // Only add padding around text content
                    if (message.isNotBlank()) {
                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isCurrentUser)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                            
                            Text(
                                text = relativeTime,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isCurrentUser)
                                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                textAlign = TextAlign.End,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    } else {
                        // Just the timestamp with minimal padding
                        Text(
                            text = relativeTime,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isCurrentUser)
                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            textAlign = TextAlign.End,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    // Only text message (no image)
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isCurrentUser)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = relativeTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isCurrentUser)
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}