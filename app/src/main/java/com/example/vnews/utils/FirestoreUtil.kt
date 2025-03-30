package com.example.vnews.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject
import javax.inject.Singleton

//@Singleton
//class FirestoreUtil @Inject constructor(
//    private val db: FirebaseFirestore,
//    private val auth: FirebaseAuth
//) {
//    //    fun sendMessage(mess: String ) {
////        val senderId = auth.currentUser?.uid ?: return
////
////        val messageData = hashMapOf(
////            "message" to mess,
////            "senderId" to senderId,
////            "timestamp" to FieldValue.serverTimestamp()
////        )
////
////        db.collection("messages")
////            .add(messageData)
////            .addOnSuccessListener { documentReference ->
////                println("Message sent with ID: ${documentReference.id}")
////            }
////            .addOnFailureListener { e ->
////                println("Error sending message: $e")
////            }
////    }
//    fun saveUserToFirestore(user: FirebaseUser) {
//        val db = FirebaseFirestore.getInstance()
//        val userRef = db.collection("users").document(user.uid)
//
//        val userData = hashMapOf(
//            "name" to (user.displayName ?: ""),
//            "email" to user.email,
//            "avatar" to (user.photoUrl?.toString() ?: "")
//        )
//
//        userRef.set(userData, SetOptions.merge())
//    }
//
//
//}
