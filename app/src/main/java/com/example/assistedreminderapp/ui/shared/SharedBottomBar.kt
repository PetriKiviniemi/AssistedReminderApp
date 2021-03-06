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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.assistedreminderapp.R

@Composable
fun SharedBottomBar(
    showLoginScreen: () -> Unit,
    showProfileScreen: (userId: Long) -> Unit = {},
    showHomeScreen: (userId: Long) -> Unit = {},
    userId: Long,
    startIndex: Int = 1,
    backgroundColor: Color = MaterialTheme.colors.primaryVariant,
)
{
    val selectedIndex = remember { mutableStateOf(startIndex) }
    BottomNavigation(
        elevation = 15.dp,
        backgroundColor = backgroundColor
    ) {
        BottomNavigationItem(icon = {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "",
                tint = MaterialTheme.colors.secondary
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
                contentDescription = "",
                tint = MaterialTheme.colors.secondary
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
                contentDescription = "",
                tint = MaterialTheme.colors.secondary
            )
        },
            label = { Text(text = "Profile") },
            selected = (selectedIndex.value == 2),
            onClick = {
                selectedIndex.value = 2
                showProfileScreen(userId)
            }
        )

    }
}
