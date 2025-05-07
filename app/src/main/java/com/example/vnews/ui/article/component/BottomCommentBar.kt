package com.example.vnews.ui.article.component

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.SendHorizontal
import com.example.vnews.utils.PermissionManager
import com.example.vnews.utils.SpeechRecognitionUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun BottomCommentBar(encodedArticlePath: String) {
    var commentText by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Create SpeechRecognitionUtil instance
    val speechRecognitionUtil = remember { SpeechRecognitionUtil(context) }
    val isListening by speechRecognitionUtil.isListening.collectAsState()
    val speechText by speechRecognitionUtil.speechText.collectAsState()

    // Permission state
    var showPermissionRequest by remember { mutableStateOf(false) }

    LaunchedEffect(speechText) {
        if (speechText.isNotEmpty()) {
            commentText = speechText
        }
    }

    // Clean up resources
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                speechRecognitionUtil.destroy()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            speechRecognitionUtil.destroy()
        }
    }

    // Handle permission request if needed
    if (showPermissionRequest) {
        PermissionManager.RequestPermission(
            permission = Manifest.permission.RECORD_AUDIO,
            onPermissionGranted = {
                showPermissionRequest = false
                speechRecognitionUtil.startListening("vi-VN")
            },
            onPermissionDenied = {
                showPermissionRequest = false
                Toast.makeText(
                    context,
                    "Microphone permission is required for speech recognition",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val scope = rememberCoroutineScope()
    if (currentUser == null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Please login to leave a comment",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        Column {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(Gray)
            )

            // Speech recognition indicator
            if (isListening) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Listening...",
                        style = TextStyle(fontSize = 14.sp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    SpeechRecognitionIndicator(isListening = true)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("Input comment") },
                    modifier = Modifier.weight(1f),
                    shape = RectangleShape,
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    )
                )

                // Speech recognition toggle button
                IconButton(
                    onClick = {
                        if (isListening) {
                            speechRecognitionUtil.stopListening()
                        } else {
                            if (SpeechRecognitionUtil.hasRecordAudioPermission(context)) {
                                speechRecognitionUtil.startListening("vi-VN")
                            } else {
                                showPermissionRequest = true
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = if (isListening) "Stop speech recognition" else "Start speech recognition",
                        tint = if (isListening) MaterialTheme.colorScheme.primary else Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(onClick = {
                    if (commentText.isNotBlank()) {
                        scope.launch {
                            val commentData = hashMapOf(
                                "content" to commentText,
                                "senderId" to (currentUser.uid),
                                "timestamp" to FieldValue.serverTimestamp()
                            )
                            db.collection("articles")
                                .document(encodedArticlePath)
                                .collection("comments")
                                .add(commentData)
                            commentText = ""
                        }
                    }
                }) {
                    Icon(
                        Lucide.SendHorizontal,
                        contentDescription = "Send",
                        tint = Gray
                    )
                }
            }
        }
    }
}