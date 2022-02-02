package com.example.assistedreminderapp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import com.example.assistedreminderapp.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.assistedreminderapp.ui.home.reminder.Reminders
import com.example.assistedreminderapp.ui.shared.SharedBottomBar
import com.google.accompanist.insets.systemBarsPadding

@Composable
fun Home(
    viewModel: HomeViewModel = viewModel(),
    showLoginScreen: () -> Unit,
    showProfileScreen: (userId: Long) -> Unit,
    userId: Long?
)
{
    val viewState by viewModel.state.collectAsState()

    /* TODO:: HANDLE BETTER */
    if(userId == null)
        return

    Surface(
        modifier = Modifier.fillMaxSize()
    )
    {
        Scaffold(
            modifier = Modifier.fillMaxWidth(),
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { /* TODO:: NAVIGATION */ },
                    contentColor = Color.Blue,
                    modifier = Modifier.padding(all = 20.dp)
                ){
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            },
            bottomBar = {
                SharedBottomBar(
                    showProfileScreen = showProfileScreen,
                    showLoginScreen = showLoginScreen,
                    userId = userId,
                )
            }
        )
        {
            //NOTE:: Content of the home view here
            Column(
                modifier = Modifier
                    .systemBarsPadding()
                    .fillMaxWidth()
            )
            {
                HomeNavBar(
                    backgroundColor = MaterialTheme.colors.secondary.copy(alpha = 0.85f),
                )

                Reminders(
                    modifier = Modifier.fillMaxSize(),
                    userId = userId
                )
            }
        }
    }
}

@Composable
fun HomeNavBar(
    backgroundColor: Color,
)
{
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colors.primary,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .heightIn(max = 24.dp)
            )
        },
        backgroundColor = backgroundColor,
        actions = {}
    )
}