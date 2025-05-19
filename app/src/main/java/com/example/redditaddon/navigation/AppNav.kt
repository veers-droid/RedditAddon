package com.example.redditaddon.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.redditaddon.activities.main.MainScreen
import com.example.redditaddon.ui.profile.ProfileScreen

@Composable
fun AppNavGraph(navController: NavHostController, factory : ViewModelProvider.Factory) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") { MainScreen(navController, factory) }
        composable("profile") { ProfileScreen(navController, factory) }
    }
}