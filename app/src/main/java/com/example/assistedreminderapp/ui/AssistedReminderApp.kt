package com.example.assistedreminderapp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.assistedreminderapp.ui.Screen
import com.example.assistedreminderapp.ui.home.Home
import com.example.assistedreminderapp.ui.login.Login
import com.example.assistedreminderapp.ui.maps.ReminderLocationMap
import com.example.assistedreminderapp.ui.profile.Profile
import com.example.assistedreminderapp.ui.reminder.Reminder
import com.google.android.gms.maps.model.LatLng

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
                userId = userId,
                showLoginScreen = { appState.navController.navigate(Screen.Login.defaultRoute()) {popUpTo(0)  {inclusive = true}}},
                showProfileScreen= { userId ->
                    appState.navController.navigate(Screen.Profile.createRoute(userId))
                },
                showReminderScreen = { userId, reminderId ->
                    appState.navController.navigate(Screen.Reminder.createRoute(userId, reminderId))
                },
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

        composable(route = Screen.Reminder.defaultRoute()) { navBackStackEntry ->
            val userId = navBackStackEntry.arguments?.getString("userId")?.toLong()
            val reminderId = navBackStackEntry.arguments?.getString("reminderId")
            val remIdLong = if(reminderId != "null") reminderId?.toLong() else null
            requireNotNull(userId) { "userId parameter wasn't found. userId parameter is required for reminder screen"}
            Reminder(
                userId = userId,
                reminderId = remIdLong,
                navController = appState.navController,
                showHomeScreen = {
                    appState.navController.navigate(Screen.Home.createRoute(userId)) {
                        popUpTo(Screen.Reminder.createRoute(userId, remIdLong)) {
                            inclusive = true
                        }
                    }
                },
                showMapLocationScreen = {
                    appState.navController.navigate(Screen.ReminderLocationMap.createRoute(userId)){
                        popUpTo(Screen.ReminderLocationMap.createRoute(userId)) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(route = Screen.ReminderLocationMap.defaultRoute()) { navBackStackEntry ->
            val userId = navBackStackEntry.arguments?.getString("userId")?.toLong()
            ReminderLocationMap(
                userId = userId,
                navigateBack = { latlng ->
                    appState.navController.previousBackStackEntry?.savedStateHandle?.set("location_data", latlng)
                    appState.navController.popBackStack(Screen.Reminder.route, false)
                }
            )
        }
    }
}