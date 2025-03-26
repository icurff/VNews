package com.example.vnews.ui.viewmodel

import android.app.Activity
import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vnews.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userName = MutableStateFlow("Guest")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userPhotoUrl = MutableStateFlow<String?>(null)
    val userPhotoUrl: StateFlow<String?> = _userPhotoUrl.asStateFlow()

    init {
        auth.currentUser?.let { user ->
            _isLoggedIn.value = true
            _userName.value = user.displayName ?: "Guest"
            _userPhotoUrl.value = user.photoUrl?.toString()
        }
    }

    fun signInWithGoogle(activity: Activity) {
        val credentialManager = CredentialManager.create(activity)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(appContext.getString(R.string.default_web_client_id))
            .setAutoSelectEnabled(true)
            .build()
        // Create the Credential Manager request
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
        viewModelScope.launch {
            try {
                val result = credentialManager.getCredential(
                    context = activity,
                    request = request
                )

                val credential = result.credential
                // convert to google id token
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val googleIdToken = googleIdTokenCredential.idToken
                // authenticate with firebase credential
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()

                authResult.user?.let { user ->
                    _isLoggedIn.value = true
                    _userName.value = user.displayName ?: "Guest"
                    _userPhotoUrl.value = user.photoUrl?.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun signOut(activity: Activity) {
        viewModelScope.launch {
            auth.signOut()
            val credentialManager = CredentialManager.create(activity)
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            _isLoggedIn.value = false
            _userName.value = "Guest"
            _userPhotoUrl.value = null

        }
    }
} 