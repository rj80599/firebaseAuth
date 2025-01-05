package com.example.firebaseauth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.firebaseauth.ui.theme.FirebaseAuthTheme
import com.google.android.gms.auth.api.identity.Identity

class MainActivity : ComponentActivity() {
    val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        authViewModel.initGoogleSignIn(this@MainActivity)
        setContent {
            FirebaseAuthTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyAppNavigation(modifier =  Modifier.padding(innerPadding),authViewModel = authViewModel)
                }
            }
        }
    }

    fun startIntentSenderForResult(
        intent: Intent,
        requestCode: Int,
        fillInIntent: Nothing?,
        flagsMask: Int,
        flagsValues: Int,
        extraFlags: Int,
        options: Nothing?
    ) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001) {
            val oneTapClient = Identity.getSignInClient(this)
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken
                authViewModel.handleSignInResult(idToken)
            } catch (e: Exception) {
                Log.e("MainActivity", "Google Sign-In failed: ${e.message}")
            }
        }
    }
}