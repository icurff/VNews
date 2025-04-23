package com.example.vnews.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class ChatMessage(
    val id: String = "",
    val message: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderAvatar: String? = null,
    val timestamp: Timestamp? = null,
    val imageUrl: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    init {
        observeMessages()
    }

    private fun observeMessages() {
        viewModelScope.launch {
            try {
                db.collection("messages")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(50)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            return@addSnapshotListener
                        }

                        viewModelScope.launch {
                            val messageList = snapshot?.documents?.mapNotNull { doc ->
                                try {
                                    val data = doc.data ?: return@mapNotNull null
                                    val senderId = data["senderId"] as? String ?: return@mapNotNull null
                                    val message = data["message"] as? String ?: ""
                                    val senderName = data["senderName"] as? String ?: ""
                                    val timestamp = data["timestamp"] as? Timestamp
                                    val imageUrl = data["imageUrl"] as? String
                                    
                                    // Fetch user avatar from user ID
                                    val userSnapshot = db.collection("users").document(senderId).get().await()
                                    val senderAvatar = userSnapshot.getString("avatar")
                                    
                                    ChatMessage(
                                        id = doc.id,
                                        message = message,
                                        senderId = senderId,
                                        senderName = senderName,
                                        senderAvatar = senderAvatar,
                                        timestamp = timestamp,
                                        imageUrl = imageUrl
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    null
                                }
                            } ?: emptyList()
                            
                            _messages.value = messageList.reversed()
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}