package com.example.vnews.ui.community.component

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import com.example.vnews.utils.ImageUtils
import com.example.vnews.utils.PermissionManager
import com.example.vnews.utils.SpeechRecognitionUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun BottomChatBar() {
    var messageText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Create SpeechRecognitionUtil instance
    val speechRecognitionUtil = remember { SpeechRecognitionUtil(context) }
    val isListening by speechRecognitionUtil.isListening.collectAsState()
    val speechText by speechRecognitionUtil.speechText.collectAsState()

    // Permission state
    var showPermissionRequest by remember { mutableStateOf(false) }

    // Image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    // Update message text with speech recognition results
    LaunchedEffect(speechText) {
        if (speechText.isNotEmpty()) {
            messageText = speechText
        }
    }

    // Clean up resources when component is disposed
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

    // Handle permission request
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

    // Function to send a message
    val sendMessage = { imageUrl: String? ->
        if ((messageText.isNotBlank() || imageUrl != null) && currentUser != null) {
            scope.launch {
                val messageData = hashMapOf(
                    "message" to messageText,
                    "senderId" to currentUser.uid,
                    "senderName" to (currentUser.displayName ?: "Guest"),
                    "timestamp" to FieldValue.serverTimestamp()
                )

                // Add image URL if present
                if (imageUrl != null) {
                    messageData["imageUrl"] = imageUrl
                }

                db.collection("messages").add(messageData)
                messageText = ""
                selectedImageUri = null
                isUploading = false
            }
        } else {
            isUploading = false
        }
    }

    Column(
        modifier = Modifier.padding(
            bottom = WindowInsets.navigationBars
                .asPaddingValues()
                .calculateBottomPadding()
        )
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(Gray)
        )

        // Selected image preview
        if (selectedImageUri != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Gray, RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected image",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )

                    // Close button to remove the image
                    IconButton(
                        onClick = { selectedImageUri = null },
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove image",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Image selected",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Input message") },
                modifier = Modifier.weight(1f),
                shape = RectangleShape,
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                )
            )

            // Image picker button
            IconButton(
                onClick = {
                    if (!isUploading) {
                        imagePickerLauncher.launch("image/*")
                    }
                },
                enabled = !isUploading
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Attach image",
                    tint = if (selectedImageUri != null) MaterialTheme.colorScheme.primary else Gray,
                    modifier = Modifier.size(24.dp)
                )
            }

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
                },
                enabled = !isUploading
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = if (isListening) "Stop speech recognition" else "Start speech recognition",
                    tint = if (isListening) MaterialTheme.colorScheme.primary else Gray,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Send button or loading indicator
            if (isUploading) {
                Box(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        if (selectedImageUri != null) {
                            isUploading = true
                            scope.launch {
                                val imageUrl = ImageUtils.uploadImage(context, selectedImageUri!!)
                                sendMessage(imageUrl)
                            }
                        } else {
                            sendMessage(null)
                        }
                    },
                    enabled = (messageText.isNotBlank() || selectedImageUri != null) && !isUploading
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.Send,
                        contentDescription = "Send",
                        tint = if (messageText.isNotBlank() || selectedImageUri != null)
                            MaterialTheme.colorScheme.primary else Gray
                    )
                }
            }
        }
    }
}