package com.example.firebaseauth

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        if (auth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
        }
    }

    fun login(email: String, password: String) {

        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signup(email: String, password: String) {

        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    fun googleSignInWithCredentialManager(
        context: Context
    ) {
        val credentialManager = CredentialManager.create(context)

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(getCredentialOptions(context))
            .build()
        _authState.value = AuthState.Loading
        // Use CoroutineScope to launch the request asynchronously
        viewModelScope.launch {
            try {
                // Get the credentials from the credential manager
                val result = credentialManager.getCredential(context, request)

                // Handle the result
                when (val credential = result.credential) {
                    is CustomCredential -> {
                        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            // Extract the Google ID token from the credential
                            val googleIdTokenCredential =
                                GoogleIdTokenCredential.createFrom(credential.data)
                            val googleTokenId = googleIdTokenCredential.idToken

                            // Create Firebase credential using Google token
                            val authCredential =
                                GoogleAuthProvider.getCredential(googleTokenId, null)

                            // Authenticate the user with Firebase
                            try {
                                val user = Firebase.auth.signInWithCredential(authCredential).await().user
                                user?.let {
                                    if (!it.isAnonymous) {
//                                        onLoginSuccess()
                                        _authState.value = AuthState.Authenticated

                                    }
                                }
                            } catch (e: Exception) {
                                _authState.value =
                                    AuthState.Error(e.message ?: "Firebase authentication failed")
                                // Handle Firebase sign-in errors
                                Log.e("GoogleSignIn", "Firebase authentication failed", e)
                            }
                        }
                    }

                    else -> {
                        // Handle cases where the credential is not a Google ID token
                        Log.e("GoogleSignIn", "Invalid credential type received")
                    }
                }
            } catch (e: NoCredentialException) {
                // Launch the sign-in flow if no credentials are available
                Log.e("GoogleSignIn", "No credentials available, launching sign-in", e)
                val intent = Intent(Settings.ACTION_ADD_ACCOUNT).apply {
                    putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
                }
                context.startActivity(intent)
            } catch (e: GetCredentialException) {
                // Handle any other errors that occur during credential fetching
                Log.e("GoogleSignIn", "Failed to get credential", e)
            }
        }
    }

    // Helper function to get the Google ID token credentials
    private fun getCredentialOptions(context: Context): CredentialOption {
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .setServerClientId(context.getString(R.string.web_client_id))
            .build()
    }


}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}