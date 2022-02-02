package com.example.assistedreminderapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class AssistedReminderAppState(val navController: NavHostController)
{
    fun navigateBack()
    {
        navController.popBackStack();
    }
}

@Composable
fun rememberAssistedReminderAppState(
    navController: NavHostController = rememberNavController()
) = remember(navController) {AssistedReminderAppState(navController)}