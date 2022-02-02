package com.example.assistedreminderapp.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.assistedreminderapp.util.viewModelProviderFactoryOf
import com.google.accompanist.insets.systemBarsPadding
import com.example.assistedreminderapp.R
import com.example.assistedreminderapp.ui.home.reminder.RemindersViewModel
import com.example.assistedreminderapp.ui.shared.SharedBottomBar

@Composable
fun Profile(
    showLoginScreen: () -> Unit,
    showHomeScreen: (userId: Long) -> Unit,
    userId: Long,
)
{
    val viewModel: ProfileViewModel = viewModel(
        key = "profile_list_$userId",
        factory = viewModelProviderFactoryOf { ProfileViewModel(userId = userId) }
    )
    val viewState by viewModel.state.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize()
    )
    {
        Scaffold(
            modifier = Modifier.fillMaxWidth(),
            bottomBar = {
                SharedBottomBar(
                    showLoginScreen = showLoginScreen,
                    showHomeScreen = showHomeScreen,
                    userId = userId,
                    startIndex = 2,
                )
            }
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .systemBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            )
            {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .systemBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                )
                {
                    Text(
                        text = "Profile view",
                        fontSize = 26.sp
                    )
                    Spacer(
                        modifier = Modifier.height(50.dp)
                    )
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = null
                    )
                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )
                    Text(
                        text = "Username:"
                    )
                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )
                    Text(
                        text = "${viewState.curUser?.username}"
                    )
                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )
                    Text(
                        text = "Password:"
                    )
                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )
                    Text(
                        text = "${viewState.curUser?.password}"
                    )
                }
            }
        }
    }
}
