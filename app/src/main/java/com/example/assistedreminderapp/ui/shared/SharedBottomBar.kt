package com.example.assistedreminderapp.ui.shared

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SharedBottomBar(
    showLoginScreen: () -> Unit,
    showProfileScreen: (userId: Long) -> Unit = {},
    showHomeScreen: (userId: Long) -> Unit = {},
    userId: Long,
    startIndex: Int = 1,
    backgroundColor: Color = MaterialTheme.colors.secondary,
)
{
    val selectedIndex = remember { mutableStateOf(startIndex) }
    BottomNavigation(elevation = 15.dp) {
        BottomNavigationItem(icon = {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Logout"
            )
        },
            label = { Text(text = "Logout") },
            selected = (selectedIndex.value == 0),
            onClick = {
                selectedIndex.value = 0
                showLoginScreen()
            }
        )
        BottomNavigationItem(icon = {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home"
            )
        },
            label = { Text(text = "Home") },
            selected = (selectedIndex.value == 1),
            onClick = {
                selectedIndex.value = 1
                showHomeScreen(userId)
            }
        )
        BottomNavigationItem(icon = {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Profile"
            )
        },
            label = { Text(text = "Profile") },
            selected = (selectedIndex.value == 3),
            onClick = {
                selectedIndex.value = 3
                showProfileScreen(userId)
            }
        )

    }
}
