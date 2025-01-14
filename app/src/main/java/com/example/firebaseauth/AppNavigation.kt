package com.example.firebaseauth

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.firebaseauth.pages.HomePage
import com.example.firebaseauth.pages.LoginPage
import com.example.firebaseauth.pages.SignupPage

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val lendingPage = if (authViewModel.authState.value is AuthState.Authenticated) "home" else "login"

    NavHost(navController = navController, startDestination = lendingPage, builder = {
        composable("login") {
            LoginPage(modifier, navController, authViewModel)
        }
        composable("signup") {
            SignupPage(modifier, navController, authViewModel)
        }
        composable("home") {
            HomePage(modifier, navController, authViewModel)
        }
    })
}

