package com.example.assistedreminderapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.assistedreminderapp.data.entity.User
import com.example.assistedreminderapp.ui.Screen
import com.example.assistedreminderapp.ui.home.Home
import com.example.assistedreminderapp.ui.login.Login
import com.example.assistedreminderapp.ui.profile.Profile
import kotlinx.coroutines.launch

@Composable
fun AssistedReminderApp(appState: AssistedReminderAppState = rememberAssistedReminderAppState())
{
    //Instead of passing navController, pass a function to navigate somewhere
    //This way we can debug and test navigation much easier (Navigation calls happen in one place)
    NavHost(
        navController = appState.navController,
        startDestination = Screen.Login.defaultRoute()
    )
    {
        composable(route = Screen.Login.defaultRoute())
        {
            Login(
                showHomeScreen = { userId ->
                    appState.navController.navigate(Screen.Home.createRoute(userId))
                }
            )
        }

        composable(route = Screen.Home.defaultRoute()) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLong()
            requireNotNull(userId) { "userId parameter wasn't found. userId parameter is required for home screen"}
            Home(
                showLoginScreen = { appState.navController.navigate(Screen.Login.defaultRoute()) {popUpTo(0)  {inclusive = true}}},
                showProfileScreen= { userId ->
                    appState.navController.navigate(Screen.Profile.createRoute(userId))
                },
                userId = userId,
            )
        }

        composable(route = Screen.Profile.defaultRoute()) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLong()
            requireNotNull(userId) { "userId parameter wasn't found. userId parameter is required for profile screen"}
            Profile(
                showLoginScreen = { appState.navController.navigate(Screen.Login.defaultRoute()) {popUpTo(0)  {inclusive = true}}},
                showHomeScreen = { appState.navController.navigate(Screen.Home.createRoute(userId)) {popUpTo(Screen.Profile.createRoute(userId)) {inclusive = true}}},
                userId = userId,
            )
        }
    }
}